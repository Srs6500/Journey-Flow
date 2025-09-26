# JourneyFlow - Your Complete Travel Companion

JourneyFlow is a comprehensive Android travel app built with Jetpack Compose and Firebase, designed to help travelers manage their trips from planning to execution.

## Features

### 🎒 Enhanced Packing List
- **Custom Categories**: Create and manage your own packing categories with custom colors
- **Custom Items**: Add personalized items to each category
- **Smart Organization**: Drag & drop reordering, category collapse/expand
- **Search Functionality**: Quickly find items across all categories
- **Progress Tracking**: Visual progress indicators and completion statistics
- **Cloud Sync**: All data synced across devices via Firebase

### ✅ Travel Tasks Management
- **Task Categories**: Organized by Passport, Transportation, Accommodation, Insurance, Health, Money, Communication, Packing, and Other
- **Priority Levels**: Low, Medium, High, and Urgent priority settings
- **Due Dates**: Set and track task deadlines with overdue alerts
- **Smart Filtering**: Filter by category, completion status, and due dates
- **Progress Tracking**: Visual task completion statistics
- **Reminders**: Push notifications for upcoming and overdue tasks

### 💰 Expense Tracking & Budget Management
- **Real-time Budget Tracking**: Set daily and total trip budgets
- **Category-based Expenses**: Track spending across Food, Transport, Accommodation, Activities, Shopping, Health, Communication, and Other
- **Visual Analytics**: Spending trends and category breakdowns
- **Budget Alerts**: Notifications when approaching or exceeding budget limits
- **Daily Spending Summary**: Track daily expenses with visual indicators
- **Export Functionality**: Export expense data for external analysis

### 👤 Profile & Settings
- **User Profile**: Manage account information and preferences
- **Data Export/Import**: Backup and restore your travel data
- **Settings Management**: Customize notifications and app behavior
- **Statistics Dashboard**: View your travel activity and usage statistics

## Technical Features

### 🔐 Authentication
- **Firebase Authentication**: Secure email/password and Google Sign-In
- **User Management**: Profile management and account settings
- **Data Security**: All user data is securely stored and encrypted

### 📱 Modern UI/UX
- **Material 3 Design**: Modern, intuitive interface following Material Design guidelines
- **Dark/Light Theme**: Automatic theme switching based on system preferences
- **Responsive Design**: Optimized for various screen sizes and orientations
- **Smooth Animations**: Fluid transitions and micro-interactions

### ☁️ Cloud Integration
- **Firebase Firestore**: Real-time database for instant data sync
- **Offline Support**: Continue using the app without internet connection
- **Data Backup**: Automatic cloud backup of all user data
- **Cross-device Sync**: Access your data from any device

### 🔔 Smart Notifications
- **Packing Reminders**: Get notified about important items to pack
- **Task Alerts**: Reminders for upcoming and overdue tasks
- **Budget Warnings**: Alerts when approaching budget limits
- **Customizable**: Control which notifications you receive

## Architecture

### 🏗️ Clean Architecture
- **MVVM Pattern**: Model-View-ViewModel architecture for maintainable code
- **Repository Pattern**: Centralized data access layer
- **Dependency Injection**: Modular and testable code structure
- **Separation of Concerns**: Clear separation between UI, business logic, and data

### 🛠️ Tech Stack
- **Kotlin**: Modern Android development language
- **Jetpack Compose**: Declarative UI framework
- **Firebase**: Backend-as-a-Service for authentication and database
- **Material 3**: Modern design system
- **Navigation Component**: Type-safe navigation
- **Coroutines**: Asynchronous programming
- **StateFlow**: Reactive state management

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Firebase project setup
- Google Services configuration

### Installation
1. Clone the repository
2. Open in Android Studio
3. Follow the detailed setup instructions in [SETUP.md](SETUP.md)
4. Configure your API keys (see Security section below)
5. Build and run the app

### 🔐 **API Keys & Security**
**IMPORTANT**: This app requires API keys for Firebase and Google Sign-In. For security reasons, these are not included in the repository.

**Setup Steps:**
1. Copy `secrets.properties.template` to `secrets.properties`
2. Add your Firebase and Google API keys to `secrets.properties`
3. Add your `google-services.json` file to the `app/` directory
4. See [SETUP.md](SETUP.md) for detailed instructions

**Security Notes:**
- API keys are stored in `secrets.properties` (excluded from git)
- `google-services.json` is excluded from version control
- Never commit sensitive data to public repositories
- Use different keys for development and production

## Usage

### First Time Setup
1. Launch the app
2. Sign up or sign in with Google
3. Set up your first trip budget
4. Create custom packing categories
5. Add your first travel tasks

### Daily Usage
1. **Packing**: Check off items as you pack them
2. **Tasks**: Complete travel-related tasks and mark them done
3. **Expenses**: Log daily expenses and track your budget
4. **Review**: Check your progress and remaining tasks

## Data Management

### Export Data
- Go to Profile → Quick Actions → Export Data
- Choose sharing method (email, cloud storage, etc.)
- Data is exported as JSON format

### Import Data
- Go to Profile → Quick Actions → Import Data
- Select exported JSON file
- Data will be imported and synced to your account

## Contributing

We welcome contributions! Please see our contributing guidelines for details.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, email support@journeyflow.app or create an issue in this repository.

## Roadmap

### Upcoming Features
- [ ] Trip planning calendar
- [ ] Weather integration
- [ ] Currency conversion
- [ ] Offline maps integration
- [ ] Travel document scanning
- [ ] Social sharing features
- [ ] Advanced analytics and insights
- [ ] Multi-language support

---

**JourneyFlow** - Making every journey organized and memorable! ✈️
