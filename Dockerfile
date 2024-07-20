# Utiliza una imagen base oficial de Java, necesaria para Android SDK
FROM openjdk:11

# Configura las variables de entorno necesarias para el SDK de Android
ENV ANDROID_HOME /opt/android-sdk-linux
ENV PATH ${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/platform-tools

# Instala dependencias necesarias
RUN apt-get update && apt-get install -y wget unzip

# Descarga y descomprime el SDK de Android
RUN wget -q "https://dl.google.com/android/repository/commandlinetools-linux-7302050_latest.zip" -O android-sdk.zip && \
    mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    unzip -q android-sdk.zip -d ${ANDROID_HOME}/cmdline-tools && \
    rm android-sdk.zip

# Acepta las licencias de SDK
RUN mkdir -p ~/.android/ && echo '### User Agreements' > ~/.android/repositories.cfg && \
    yes | ${ANDROID_HOME}/cmdline-tools/bin/sdkmanager --licenses

# Instala las plataformas y herramientas necesarias
RUN ${ANDROID_HOME}/cmdline-tools/bin/sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3"

# Establece el directorio de trabajo
WORKDIR /app

# Copia el código fuente de la aplicación en el contenedor
COPY . .

# Comando para construir la aplicación (ajusta según tu proyecto)
RUN ./gradlew assembleDebug

# Configura el comando por defecto
CMD ["./gradlew", "assembleDebug"]
