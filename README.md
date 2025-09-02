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


---

## 스마트 옷장 App 소개
![스마트옷장app소개](https://raw.githubusercontent.com/garden0507/2025ESWContest_free_1076/main/images/스마트옷장app소개.png)
스마트 옷장 App은 사용자가 보유한 옷을 더 쉽게 정리하고 한눈에 관리할 수 있도록 돕는 동시에, 원격으로 원하는 옷을 찾아 기기에 명령을 내릴 수 있는 애플리케이션입니다. 
또한 사용자에게 옷장에 넣고자 하는 옷에 대한 상세정보를 등록하도록 유도하고, 실시간 날씨정보를 가져오면서 이 정보를 통해 옷 추천을 해주는 기능을 제공하고 있습니다.

---




## App 동작 흐름도
1. 옷 커스텀하기
![옷커스텀하기](https://raw.githubusercontent.com/garden0507/2025ESWContest_free_1076/main/images/옷커스텀하기.png)
옷 커스텀 기능은 옷장에 넣고자 하는 옷 사진을 App에 저장하는 과정을 수행합니다. 먼저 App을 실행하자마자 나오는 메인 화면에서 옷 커스텀하기 버튼을 누르면 사용자의 핸드폰 갤러리를 App에서 접근합니다. 이때 등록하고자 하는 옷 사진을 선택하면 App 내부에 저장이 됩니다. 이때 앱은 자동으로 옷 색상을 계산하고 이름을 자동으로 지정하여, 옷 추가 시 상세 정보가 자동 입력되도록 지원합니다.

---
2. 옷 추가하기
![옷추가하기](https://raw.githubusercontent.com/garden0507/2025ESWContest_free_1076/main/images/옷추가하기.png)
옷 추가 기능은 실제 옷장에 있는 옷을 앱에서 확인하고 관리할 수 있도록, 메인 화면에 옷 정보를 보여주는 기능입니다. 옷 추가 버튼을 누르면 App이 옷장 내에 비어있는 곳이 사용자 앞에 위치하도록 옷장 기기에 명령을 내립니다. App 화면에서는 옷 이름을 지정하는 화면이 나오고, 커스텀 옷 선택하기 버튼을 통해 기존에 등록된 옷 리스트에서 선택할 수 있습니다. 선택한 옷은 앱이 자동으로 이름을 지정하며, 이후 상·하의 여부, 옷 길이, 색상 등의 상세 정보를 입력 후 다음 버튼을 클릭합니다. (색상은 커스텀 과정에서 계산된 값이 자동으로 등록) 최종 완료 버튼을 누르면, 등록한 옷 정보가 메인 화면에 표시되어 옷장에 보관된 옷을 한눈에 확인할 수 있습니다.
---
3. 옷 찾기
![옷찾기](https://raw.githubusercontent.com/garden0507/2025ESWContest_free_1076/main/images/옷찾기.png)
옷 찾기 기능은 메인 화면에 표시된 옷 리스트에서 사용자가 원하는 옷을 찾는 기능입니다. 사용자는 리스트 오른쪽에 있는 찾기 버튼을 누르면 해당 옷의 위치를 확인할 수 있으며, App은 연결된 옷장 기기에 명령을 내려 지정된 옷이 사용자 앞으로 이동하도록 제어합니다. 이를 통해 실제 옷장에서 원하는 옷을 빠르게 꺼낼 수 있습니다.
옷 추천 기능은 옷을 찾은 이후 사용자의 선택에 따라 추가적으로 옷을 추천을 제공하는 기능입니다. 찾기 버튼 실행 후 “옷을 찾았으면 확인 버튼 클릭” 라는 메시지가 표시되며, 확인 버튼을 누르면 앱이 자동으로 옷을 추천합니다. 추천 과정에서는 상·하의 조합 유무와 실시간 날씨 정보를 기반으로 관련성이 높은 옷이 옷 추천 리스트에 표시되어, 사용자가 더욱 효율적으로 옷을 선택할 수 있습니다.
이후 다시 찾기 버튼을 누르면 옷 찾기 기능이 재실행되며, 최종적으로 옷을 찾은 이후에는 App이 해당 옷을 리스트에서 자동으로 제거합니다.
---

## 전체 동작 흐름도
![전체동작흐름도](https://raw.githubusercontent.com/garden0507/2025ESWContest_free_1076/main/images/전체동작흐름도.png)

---
# Environment

## Embedded
![C](https://img.shields.io/badge/C-A8B9CC?logo=c&logoColor=white)
![C++](https://img.shields.io/badge/C%2B%2B-00599C?logo=cplusplus&logoColor=white)
![Arduino](https://img.shields.io/badge/Arduino-00979D?logo=arduino&logoColor=white)

## Frontend
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)


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
