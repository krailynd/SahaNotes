# 🧘 SahaNotes

> **La herramienta definitiva de productividad minimalista.**
> Un clon avanzado de Notion/Obsidian construido con **JavaFX** y **Tecnologías Web**.

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-21-007396?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

## 📋 Descripción

**SahaNotes** es una aplicación de escritorio diseñada para estudiantes, desarrolladores y escritores que buscan un entorno de trabajo libre de distracciones. Combina la potencia de un sistema de archivos local con la elegancia de una interfaz moderna y oscura.

Integra edición de **Markdown** en tiempo real, visualización de **Excalidraw**, gestión de **Tareas (To-Do)** y un **Modo Monje (Focus Mode)** para máxima concentración.

## ✨ Características Principales

* **🎨 Interfaz Moderna:** Diseño "Dark Mode" inspirado en Obsidian, con controles de ventana personalizados estilo Windows 11.
* **📝 Editor Markdown Híbrido:**
    * Soporte para **Negrita**, *Cursiva*, Títulos, Tablas e Imágenes locales.
    * Renderizado HTML en tiempo real usando `WebView` y CSS personalizado.
    * Soporte para **Checkboxes interactivos** `[ ]` y `[x]`.
* **🧘 Modo Monje (Focus Mode):** Oculta todos los paneles laterales y menús con un solo clic (o tecla `ESC`) para una experiencia de escritura inmersiva a pantalla completa.
* **📂 Gestión de Workspace:**
    * Creación automática de estructura de carpetas (`notes`, `excalidraw`, `tareas`, `img`, etc.).
    * Explorador de archivos integrado con iconos dinámicos.
    * Panel lateral colapsable (Mini-Sidebar) con tooltips.
* **🚀 Integración con Excalidraw:** Visualiza y crea archivos `.excalidraw` directamente dentro de la aplicación mediante un visor web integrado.
* **📅 Widgets de Productividad:**
    * Reloj Digital en tiempo real.
    * Calendario mensual integrado.
    * Lista de Tareas Rápidas (To-Do List).

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java 21 (JDK 21)
* **Framework UI:** JavaFX 21 (Modular)
* **Gestor de Dependencias:** Apache Maven
* **Componentes Clave:**
    * `javafx-web`: Para renderizado de Markdown y Excalidraw.
    * `CommonMark`: Para el parseo de Markdown a HTML.
    * `Gson`: Para persistencia de datos (Login/Usuarios).
    * `Ikonli`: Para iconografía vectorial.

## 🚀 Instalación y Compilación

Para ejecutar este proyecto en tu máquina local, necesitas tener instalado **JDK 21** y **Maven**.

### 1. Clonar el repositorio
```bash
git clone [https://github.com/krailynd/SahaNotes.git](https://github.com/krailynd/SahaNotes.git)
cd SahaNotes
