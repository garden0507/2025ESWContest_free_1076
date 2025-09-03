/*
  스마트 옷장 아두이노 구현
  - 방향 최적화 (1~4 정방향, 5→역3 / 6→역2 / 7→역1, 0/8→무동작)
  - 홀센서: 정방향 D11, 역방향 D12 (방향에 따라 다른 핀 사용)
  - 통신부: AT → CWMODE → CWJAP → CIFSR → CIPMUX → CIPSERVER (응답 시리얼 출력)
  - 앱 패킷: "+IPD:<idx,clothCount>"  예) "+IPD,52:3,2"
  - 예외: n=0이면 무동작
*/

#include <SoftwareSerial.h>

/*──────────── 모터·센서 핀 ────────────*/
#define ENA_PIN      10
#define IN1_PIN       9
#define IN2_PIN       8
#define HALL_FWD_PIN 11   // 정방향용 홀센서
#define HALL_REV_PIN 12   // 역방향용 홀센서

/*──────────── 속도 테이블 ────────────*/
#define SPEED_0  60
#define SPEED_1  60
#define SPEED_2  60
#define SPEED_3  60
#define SPEED_4  60
int currentSpeed = SPEED_0;

/*──────────── 급제동 유지 시간 ────────────*/
#define BRAKE_MS 500   // 급제동 유지 시간(ms)

/*──────────── Wi-Fi(ESP-01 AT) 통신 ────────────*/
SoftwareSerial esp(2, 3);  // UNO: D2(RX)←ESP TX, D3(TX)→ESP RX
const char *SSID = "JM notebook";
const char *PASS = "1234567890a";

/*──────────── AT 유틸 ────────────*/
void at(const String &cmd, uint16_t wait = 3000) {
  esp.println(cmd);
  uint32_t t0 = millis();
  while (millis() - t0 < wait) {
    while (esp.available()) {
      Serial.write(esp.read());
    }
  }
}

/*──────────── 모터 측 통신규약 정의(인덱스와 옷 수량을 수신받으며 구분자는 콤마를 사용) ────────────*/
// +IPD,<id>,<len>:payload  또는 +IPD,<len>:payload 허용 (예: "3,2")
bool readPayload(int &outIdx, int &outCloth) {
  if (!esp.available()) return false;
  String s = esp.readStringUntil('\n');
  s.trim();
  if (!s.startsWith("+IPD")) return false;

  int colon = s.indexOf(':'); if (colon < 0) return false;
  String payload = s.substring(colon + 1); // "3,2"
  payload.trim();

  int comma = payload.indexOf(',');
  if (comma < 0) return false;           // 단일 숫자는 무시(예외처리)
  outIdx   = payload.substring(0, comma).toInt();
  outCloth = payload.substring(comma + 1).toInt();
  return true;
}

/*──────────── 모터 제어부 ────────────*/
void startMotor(int dir) {
  digitalWrite(IN1_PIN, dir > 0);
  digitalWrite(IN2_PIN, dir < 0);
  analogWrite(ENA_PIN, currentSpeed);
}
void stopMotor() {
  analogWrite(ENA_PIN, 0);
  digitalWrite(IN1_PIN, LOW);
  digitalWrite(IN2_PIN, LOW);
}
// 급제동(현재값 유지)
void brakeMotor() {
  digitalWrite(IN1_PIN, HIGH);
  digitalWrite(IN2_PIN, HIGH);
  analogWrite(ENA_PIN, 255);
  delay(BRAKE_MS);
  stopMotor();
}

/*──────────── 옷 개수에 따른 정밀 속도 제어────────────*/
void setSpeedByClothCount(int c) {
  if (c <= 0) { currentSpeed = SPEED_0; return; }
  switch (c) {
    case 1: currentSpeed = SPEED_1; break;
    case 2: currentSpeed = SPEED_2; break;
    case 3: currentSpeed = SPEED_3; break;
    case 4: currentSpeed = SPEED_4; break;
    default: currentSpeed = SPEED_3; break; // 기본 속도
  }
}

/*──────────── 방향 최적화: n=abs(idx) ────────────*/
// n=0/8 → 무동작, 1~4 → 정방향 n스텝, 5→역3 / 6→역2 / 7→역1
void computeDirAndSteps(int idx, int &dir, int &steps) {
  int n = abs(idx);

  if (n == 0) { dir = 0; steps = 0; return; }      // 0 → 무동작
  if (n > 8) n = ((n - 1) % 8) + 1;                // 9→1, 10→2, ...

  if (n <= 3) {                                    // 1,2,3
    dir = +1; steps = n;
  } else if (n == 4) {                             // 4는 그대로 4(정방향)
    dir = +1; steps = 4;
  } else if (n <= 7) {                             // 5,6,7 → 역방향 (8-n)
    dir = -1; steps = 8 - n;                       // 5→3, 6→2, 7→1
  } else {                                         // n==8 → 무동작
    dir = 0; steps = 0;
  }
}

/*──────────── 1/8스텝 이동 (정방향과 역방향 동작을 위해 홀센서를 두개 사용) ────────────*/

void moveOptimized(int idx) {
  int dir, steps;
  computeDirAndSteps(idx, dir, steps);

  if (steps == 0) {
    Serial.println("이동 없음");
    return;
  }

  // 진행 방향에 따라 사용할 홀센서 선택
  const int sensorPin = (dir > 0) ? HALL_FWD_PIN : HALL_REV_PIN;

  Serial.print(dir > 0 ? "정방향 " : "역방향 ");
  Serial.print("1/8스텝×"); Serial.print(steps);
  Serial.print("  speed=");  Serial.println(currentSpeed);

  // 전체 이동에 대한 상한(예외처리)
  const unsigned long moveDeadline =
      millis() + (unsigned long)(steps * STEP_TIMEOUT_MS + BRAKE_MS + 500);

  for (int i = 0; i < steps; ++i) {
    // 스텝별 타임아웃 시각
    const unsigned long stepDeadline = millis() + STEP_TIMEOUT_MS;

    // 모터 시작
    startMotor(dir);

    // 에지 검출: H->L (풀업 기준)
    bool stepped = false;
    bool prev = digitalRead(sensorPin);

    while (millis() < stepDeadline) {
      bool now = digitalRead(sensorPin);
      if (prev && !now) {                   // 하강엣지 감지 = 스텝 1회 완료
        Serial.print("  · 스텝 "); Serial.println(i + 1);
        stepped = true;
        break;
      }
      prev = now;
      delayMicroseconds(150);              // 디바운싱
    }

    // 스텝 종료: 급제동 → 정지
    brakeMotor();

    // 스텝 타임아웃: 홀센서 미검출시 모터 정지 로직
    if (!stepped) {
      Serial.print("ERR: hall timeout at step ");
      Serial.println(i + 1);
      stopMotor();
      return;
    }

    // 스텝 간 관성 억제(필요 시 300~900ms로  튜닝)
    if (i < steps - 1) delay(700);

    // 응답시간 초과 시 타임아웃 로직
    if (millis() > moveDeadline) {
      Serial.println("ERR: overall move timeout");
      stopMotor();
      return;
    }
  }

  Serial.println("==> 완료");
}

/*──────────── setup / loop ────────────*/
void setup() {
  Serial.begin(9600);
  esp.begin(9600);

  pinMode(IN1_PIN, OUTPUT);
  pinMode(IN2_PIN, OUTPUT);
  pinMode(ENA_PIN, OUTPUT);
  stopMotor();

  // 홀센서 핀 설정(정/역 각각)
  pinMode(HALL_FWD_PIN, INPUT_PULLUP);
  pinMode(HALL_REV_PIN, INPUT_PULLUP);

  // 통신부
  Serial.println("ESP8266 초기화 중…");
  at("AT");
  at("AT+CWMODE=1");
  at("AT+CWJAP=\"" + String(SSID) + "\",\"" + String(PASS) + "\"", 10000);
  at("AT+CIFSR");    // IP 확인
  at("AT+CIPMUX=1");
  at("AT+CIPSERVER=1,8080");
  Serial.println("Wi-Fi 준비 완료! 통신을 시작하자! \n");
}

void loop() {
  int idx, cloth;
  if (!readPayload(idx, cloth)) return;

  Serial.print("idx="); Serial.print(idx);
  Serial.print("  cloth="); Serial.println(cloth);

  setSpeedByClothCount(cloth);
  moveOptimized(idx);
}
