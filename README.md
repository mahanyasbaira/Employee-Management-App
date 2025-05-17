# 📱 Employee Management App | IB Computer Science IA

<p align="center">
  <img src="https://your-cdn.com/employee-management-banner.png" alt="Employee Management App Banner" width="800"/>
</p>

[![Android Studio](https://img.shields.io/badge/Android%20Studio-%E2%89%A510.0.0-brightgreen)](https://developer.android.com/studio) [![Firebase](https://img.shields.io/badge/Firebase-%5E9.0-orange)](https://firebase.google.com) [![Kotlin](https://img.shields.io/badge/Kotlin-%5E1.8-blue)](https://kotlinlang.org)

This repository contains the source code and documentation for my **IB Computer Science Internal Assessment**.  
The project is a native Android–based Employee Management System built with Android Studio and Firebase. It streamlines administrative tasks for a financial/accounts head by providing user authentication, a clean admin dashboard, real-time database integration, and task reminders.

---

## 🚀 Key Features

- 🔐 **Secure Login & Authentication**  
  - Firebase Authentication (email/password)  
  - Role-based access: Admin vs. Standard User  

- 👥 **Employee CRUD Operations**  
  - Create, Read, Update & Delete employee records  
  - Profile photos, contact info, department assignment  

- 📊 **Admin Dashboard**  
  - At-a-glance statistics: total employees, active tasks, pending approvals  
  - Search & filter by department, status, hire date  

- 🌐 **Real-Time Database**  
  - Firebase Firestore for live data sync  
  - Offline support with automatic sync when back online  

- ⏰ **Task Reminders & Notifications**  
  - In-app reminders using AlarmManager  
  - Push notifications for overdue tasks (via Firebase Cloud Messaging)  

- 🗂️ **Data Export**  
  - Export employee lists or reports as CSV (local share)  

- ⚙️ **Settings & Preferences**  
  - Theme switch (Light / Dark)  
  - Notification scheduling options  

---

## 📦 Tech Stack

| Component                  | Technology / Library                         |
| -------------------------- | -------------------------------------------- |
| **Language**               | Kotlin                                       |
| **IDE**                    | Android Studio Arctic Fox (2020.3.1)         |
| **Authentication**         | Firebase Auth                                |
| **Database**               | Firebase Firestore                           |
| **Cloud Messaging**        | Firebase Cloud Messaging                     |
| **UI Toolkit**             | Android Jetpack (ViewBinding, Material)      |
| **Notifications**          | Android AlarmManager & NotificationManager   |
| **Data Export**            | OpenCSV                                      |

---

## 🔧 Installation & Setup

1. **Clone the repo**  
   ```bash
   git clone https://github.com/mahanyasbaira/employee-management-app-ia.git
   cd employee-management-app-ia
