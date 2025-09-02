# 2025ESWContest_free_1076

## 👕 Intro
**Show me the Fassion** 팀은 스마트 옷장 기반의 패션 관리 및 추천 서비스를 개발합니다.  
사용자가 옷을 더 쉽고 효율적으로 관리하며, 날씨·취향·상황에 맞는 코디 추천을 받을 수 있도록 돕는 것을 목표로 합니다.

---

## 💡 Inspiration
매일 아침 옷을 고르는 데 드는 시간과 고민을 줄이고 싶다는 아이디어에서 출발했습니다.  
또한 단순한 자동화 기능을 넘어, **포용적 디자인**을 통해 거동이 불편한 사람이나 색각 이상자도 편리하게 사용할 수 있는 옷장을 지향합니다.  

- 색상·계절·날씨 기반의 코디 추천
- IoT 기술을 활용한 옷 자동 회전/추출
- 음성 명령 및 앱 제어로 사용자 접근성 향상

---

## 🔧 Features
- 📱 **스마트폰 앱 연동**: 옷 관리 및 코디 추천 확인
- 🎨 **색상 기반 추천 알고리즘**: RGB/HSV 규칙을 기반으로 상·하의 매칭
- ⚙️ **하드웨어 기능**: 옷 자동 회전, LED 조명, 자기장 센서 기반 위치 인식
- 🌐 **IoT 연동**: Wi-Fi 모듈을 통한 제어 및 데이터 연동

---

## 👨‍👩‍👧‍👦 Team
- **Hardware**: 프레임 제작, 모터 제어, 센서 통합
- **Software**: Android 앱 개발, 추천 알고리즘 구현
- **Design & Planning**: 사용자 시나리오 설계, UX/UI 기획

---

## 🚀 Goal
-스마트 옷장을 통해 단순한 자동화 기기를 넘어, **패션과 기술이 융합된 새로운 사용자 경험**을 제공하는 것을 목표로 합니다.

---

## 스마트 옷장 구조 소개
![스마트옷장구조](https://raw.githubusercontent.com/garden0507/2025ESWContest_free_1076/main/images/스마트옷장구조.png)
-스마트 옷장 App은 사용자가 보유한 옷을 더 쉽게 정리하고 한눈에 관리할 수 있도록 돕는 동시에, 원격으로 원하는 옷을 찾아 기기에 명령을 내릴 수 있는 애플리케이션입니다. 또한 사용자에게 옷장에 넣고자 하는 옷에 대한 상세정보를 등록하도록 유도하고, 실시간 날씨정보를 가져오면서 이 정보를 통해 옷 추천을 해주는 기능을 제공하고 있습니다.

---

## 스마트 옷장 App 소개
![스마트옷장app소개](https://raw.githubusercontent.com/garden0507/2025ESWContest_free_1076/main/images/스마트옷장app소개.png)

---




## App 동작 흐름도
1. 옷 커스텀하기
![옷커스텀하기](https://raw.githubusercontent.com/garden0507/2025ESWContest_free_1076/main/images/옷커스텀하기.png)


---
2. 옷 추가하기
![옷추가하기](https://raw.githubusercontent.com/garden0507/2025ESWContest_free_1076/main/images/옷추가하기.png)

---
3. 옷 찾기
![옷찾기](https://raw.githubusercontent.com/garden0507/2025ESWContest_free_1076/main/images/옷찾기.png)

---

## 전체 동작 흐름도
![전체동작흐름도](https://raw.githubusercontent.com/garden0507/2025ESWContest_free_1076/main/images/전체동작흐름도.png)

---


## Project Structure
```
├── App
│   ├── UI
│   │   ├── MainScreen.kt
│   │   ├── DetailScreen.kt
│   │   ├── RecommendationDialog.kt
│   │   └── Components.kt
│   │
│   ├── Util
│   │   ├── NetworkUtil.kt
│   │   ├── IndexUtil.kt
│   │   ├── StorageUtil.kt
│   │   └── ColorUtils.kt
│   │
│   └── Weather
│       ├── WeatherClient.kt
│       └── Condition.kt
│
└── arduino_src
    ├── src
    │   ├── MONITOR_control.ino
    │   └── MOTOR_control.ino
    └── include
        ├── Arduino_GFX_Library
        ├── SPI
        ├── SoftwareSerial
        └── XPT2046_Touchscreen
```
