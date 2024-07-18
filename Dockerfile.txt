# Use an official openjdk runtime as a parent image
FROM openjdk:11-jdk-slim

# Install required dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# Set environment variables for Android SDK
ENV ANDROID_HOME /sdk
ENV PATH ${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/platform-tools

# Download and install Android SDK tools
RUN wget -q https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip -O android-sdk-tools.zip && \
    mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    unzip android-sdk-tools.zip -d ${ANDROID_HOME}/cmdline-tools && \
    rm android-sdk-tools.zip

# Install Android SDK components
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3"

# Copy the project files into the container
COPY . /app

# Set the working directory
WORKDIR /app

# Build the Android project
RUN ./gradlew build

# Set the entrypoint (optional, for example purposes)
ENTRYPOINT ["./gradlew"]