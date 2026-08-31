# Installation Manual - Stock Inventory Tracker

Student: PAN YAOXIANG  
Student ID: S1041399  
Module: CN6035 Mobile and Distributed Systems

## Requirements

- Android Studio Meerkat 2024.3.1 Patch 1 or later
- Embedded JDK 17
- Android SDK Platform 35 for compilation
- Android API 36 system image for the tested emulator (or another compatible Android device)
- Internet access during the first Gradle dependency download

## Build configuration used in the tested project

- Android Gradle Plugin: 8.9.2
- Gradle wrapper: 8.11.1
- compileSdk: 35
- targetSdk: 35
- minSdk: 24
- Java: 17

## Steps

1. Clone or download this repository.
2. Open Android Studio.
3. Choose **Open** and select the repository root folder, not the `app` subfolder.
4. Use the **Embedded JDK 17** if Android Studio asks for a Gradle JDK.
5. Wait for Gradle Sync to finish.
6. If SDK Platform 35 is missing, open **Tools > SDK Manager** and install Android SDK Platform 35.
7. Open **Tools > Device Manager** and select or create an Android Virtual Device. The submitted project was tested on **Medium Phone API 36 arm64**.
8. Select the `app` run configuration.
9. Click the green **Run** button.
10. Confirm that the Stock Overview page opens directly without a login screen.

## Verified runtime result

On 31 August 2026, the project built successfully in Android Studio and ran on a Medium Phone API 36 emulator. The Stock Overview, stock adjustment, validation, history, monthly graph and About screens were exercised successfully.

## Reset sample data

To restore the seeded sample products and adjustment history, uninstall the application from the emulator and run it again. This recreates the local SQLite database.
