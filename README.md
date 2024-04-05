# Food Chat

Food Chat provides a chat interface integrated with the Google Gemini API to interact with a chatbot.

![1](https://github.com/Getir-Android-Kotlin-Bootcamp/OfficeHoursProject-group5/assets/24704582/eb77abcf-de39-4367-9443-ee2d0deed110)

## Requirements
This project requires one of the following Android Studio versions:
- Android Studio Iguana (version 2023.2.1)
- Android Studio Hedgehog (version 2023.1.1 Patch 2)


## Getting Started

Before starting, you need to add a `local.properties` file to your project and define the path to your Android SDK in it. Follow these steps:

1. Create a `local.properties` file in the root directory of your project 
2. Open the `local.properties` file
3. Add the following line to specify the path to your Android SDK, replacing `sdk.dir=C\:\\Users\\user1\\AppData\\Local\\Android\\Sdk` with the actual path on your system

## Demo



https://github.com/Getir-Android-Kotlin-Bootcamp/OfficeHoursProject-group5/assets/24704582/a95a0503-b738-4d95-8331-57ed175c3de6

## Custom Methods

### 1. `resizeWindow()`

This method adjusts the screen size when the keyboard is opened to enhance readability. It achieves this by adjusting the size of the page when the keyboard is opened.

```kotlin
private fun resizeWindow(){
    binding.etText.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
    })
}





