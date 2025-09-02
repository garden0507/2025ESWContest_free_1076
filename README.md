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
<img width="1873" height="918" alt="Image" src="https://github.com/user-attachments/assets/3a30e734-b941-48a0-a71f-0e413556b47a" />

---

## 스마트 옷장 App 소개
<img width="1416" height="1009" alt="image" src="https://github.com/user-attachments/assets/c990647e-827c-494a-9234-3035395e177f" />

---




## App 동작 흐름도
1. 옷 커스텀하기
<img width="1499" height="428" alt="image" src="https://github.com/user-attachments/assets/a9d3044f-8410-4db6-8f62-4dec244f93ea" />

---
2. 옷 추가하기
<img width="1560" height="846" alt="image" src="https://github.com/user-attachments/assets/3d192b53-4f0c-41b3-8648-04d7cf6cf257" />

---
3. 옷 찾기
<img width="1440" height="881" alt="image" src="https://github.com/user-attachments/assets/3d2cbe90-a72b-423f-88f9-50a806f75664" />

---

## 전체 동작 흐름도
<img width="1731" height="961" alt="image" src="https://github.com/user-attachments/assets/592ddafa-6fd0-4e16-923c-d86f58e81660" />

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
