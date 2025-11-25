# 🎯 Smart Habit Tracker with AI Coaching

A full-stack AI-powered habit tracking application built with Spring Boot (Java) backend, React (JavaScript) frontend, PostgreSQL database, and local AI using Ollama.

## ✨ Features

### 🔐 Authentication
- Secure user registration and login
- JWT-based authentication
- Protected routes and API endpoints
- Password hashing with BCrypt

### 📊 Habit Tracking
- Create, update, and delete habits
- Categorize habits (Health, Fitness, Study, Sleep, Work, Personal, Other)
- Daily habit completion check-ins
- Visual calendar heatmap 
- Streak tracking
- Consistency percentage calculation

### 📈 Analytics Dashboard
- Weekly consistency trends 
- Category performance 
- Category streaks 
- Overall statistics and insights
- animated charts using Chart.js

### 🤖 AI Coaching 
- **Automatic weekly reports** generated every Sunday at 9 AM
- AI analyzes your habit performance
- Identifies strengths and weaknesses
- Provides personalized improvement plans
- Motivational messages
- **100% offline** - uses local Ollama models
- Timeline view of all past reports

### 🎨 UI/UX
- Smooth animations and transitions
- Fully responsive 
- Tailwind CSS styling

## 🚀 Setup Instructions

### Prerequisites

1. **Java 17+** - [Download](https://adoptium.net/)
2. **Maven 3.6+** - [Download](https://maven.apache.org/)
3. **Node.js 18+** - [Download](https://nodejs.org/)
4. **PostgreSQL 14+** - [Download](https://www.postgresql.org/download/)
5. **Ollama** - [Download](https://ollama.ai/)

### Step 1: Install Ollama and Download Model

```bash
# Install Ollama (varies by OS)
# Windows: Download from https://ollama.ai/download
# macOS: brew install ollama
# Linux: curl -fsSL https://ollama.ai/install.sh | sh

# Start Ollama service
ollama serve

# In another terminal, pull a model
ollama pull mistral
# or
ollama pull llama3
# or
ollama pull phi3
```

### Step 2: Setup PostgreSQL Database

```bash
# Create database
psql -U postgres
CREATE DATABASE habit_tracker;
\q
```

### Step 3: Configure Backend

1. Navigate to backend directory:
```bash
cd backend
```

2. Update `src/main/resources/application.yml`
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/habit_tracker
    username: postgres
    password: your_password
```

3. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

Backend will run on `http://localhost:8080`

### Step 4: Setup Frontend

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start development server:
```bash
npm run dev
```

Frontend will run on `http://localhost:3000`


