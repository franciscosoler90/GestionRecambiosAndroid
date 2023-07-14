Gestión de recambios
Es una aplicación Android desarrollada completamente en Kotlin para la
gestión de recambios, gestión de inventarios, picking, mercancías y
recambios, es interoperable con X.Auto DMS a través de la conexión a su
base de datos Microsoft SQL Server.
Tecnologías empleadas:
Entorno de desarrollo integrado (IDE): Android Studio es el entorno
de desarrollo oficial de Google para Android desde 2017. Es una
herramienta poderosa que facilita la creación de aplicaciones Android.
Programación: Kotlin es un lenguaje de programación moderno y conciso
que se utiliza para desarrollar aplicaciones Android. Es el lenguaje oficial
respaldado por Google para el desarrollo de Android desde 2017. Kotlin está
diseñado para ser totalmente interoperable con Java
Diseño de las interfaces: XML Se utiliza para diseñar las interfaces de
usuario en Android. Permite definir la estructura y el aspecto visual de los
componentes de las interfaces.
Bases de datos: Microsoft SQL Server es una base de datos relacional
ampliamente utilizada. Se utiliza en la aplicación Android para almacenar y
gestionar datos de inventarios, picking y gestión de mercancías.
APIs:
jTDS es un controlador JDBC 3.0 100% puro de Java (tipo 4) de código
abierto para Microsoft SQL Server (6.5, 7, 2000, 2005, 2008, 2012) y
Sybase ASE (10, 11, 12, 15). El controlador jTDS permite la conexión y la
interacción con la base de datos Microsoft SQL Server desde la aplicación
Android. La versión específica que se utiliza es la 1.3.1.
Enlace:
https://mvnrepository.com/artifact/net.sourceforge.jtds/jtds/1.3.1
3
En el archivo build.gradle, se encuentran las dependencias que se deben
agregar al proyecto para utilizar las librerías y APIs necesarias.
Es recomendable activar las opciones de desarrollador en el terminal
Android. Para ello, sigue estos pasos:
1. En el terminal Android, ve a "Ajustes" -> "Información del
teléfono".
2. Desliza hacia abajo hasta ver la opción "Número de compilación".
3. Pulsa siete veces seguidas sobre la opción "Número de
compilación" para activar el modo desarrollador.
A continuación, activa la depuración por USB para la conexión con el
ordenador y mantén la conexión por cable para ejecutar las pruebas desde
Android Studio. Para permitir la depuración por USB en el terminal móvil,
sigue las instrucciones específicas del terminal o busca la opción en la
configuración del desarrollador.
Especificaciones del terminal móvil Honeywell EDA51K:
• Versión de Android: 10
• Resolución de pantalla: 480x800 pixeles
• Conexión USB: tipo C
• Memoria RAM: 3 GB
• Batería de 4000 mAh, 12 horas de uso
• Lector de código de barras
Software adicional:
scrcpy
Esta aplicación proporciona visualización y control de dispositivos Android
conectados vía USB o sobre TCP/IP. No requiere ningún acceso root.
Funciona en GNU/Linux, Windows y macOS.
https://github.com/Genymobile/scrcpy
