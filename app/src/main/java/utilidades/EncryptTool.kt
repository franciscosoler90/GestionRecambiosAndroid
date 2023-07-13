/*
 * © Copyright 2023 , Francisco José Soler Conchello
 */

package utilidades

class EncryptTool {

    //Declara una lista con los caracteres especiales de cifrado
    private val cargaTabla2 = listOf(
        ' ',
        '!',
        '"',
        '#',
        '$',
        '%',
        '&',
        '(',
        ')',
        '*',
        '+',
        ',',
        '-',
        '.',
        '/',
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        ':',
        ';',
        '<',
        '=',
        '>',
        '?',
        '@',
        'A',
        'B',
        'C',
        'D',
        'E',
        'F',
        'G',
        'H',
        'I',
        'J',
        'K',
        'L',
        'M',
        'N',
        'O',
        'P',
        'Q',
        'R',
        'S',
        'T',
        'U',
        'V',
        'W',
        'X',
        'Y',
        'Z',
        '[',
        "\"",
        ']',
        '^',
        '_',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j',
        'k',
        'l',
        'm',
        'n',
        'o',
        'p',
        'q',
        'r',
        's',
        't',
        'u',
        'v',
        'w',
        'x',
        'y',
        'z',
        '{',
        '|',
        '}',
        '–',
        '¡',
        '¦',
        '¿',
        'À',
        'Á',
        'Â',
        'Ã',
        'Ä',
        'Å',
        'Ç',
        'È',
        'É',
        'Ê',
        'Ë',
        'Ì',
        'Í',
        'Î',
        'Ï',
        'Ñ',
        'Ò',
        'Ó',
        'Ô',
        'Õ',
        'Ö',
        'Ù',
        'Ú',
        'Û',
        'Ü',
        'à',
        'á',
        'â',
        'ã',
        'ä',
        'å',
        'æ',
        'ç',
        'è',
        'é',
        'ê',
        'ë',
        'ì',
        'í',
        'î',
        'ï',
        'ñ',
        'ò',
        'ó',
        'ô',
        'õ',
        'ö',
        'ù',
        'ú',
        'û',
        'ü',
        'ý',
    )

    //Función que devuelve una cadena de texto en función de si tiene que encriptar
    //o desencriptar la cadena de texto de entrada
    fun encryptString(encriptar: Boolean, cadenaEntrada: String) : String{

        //Cadena de texto vacia
        var cadenaSalida = ""

        //Inicializa una tabla con los valores especiales de encriptación
        val cargaTabla = listOf(17, 3, 102, -15, 8, 25, -114, 15, 2, -5)

        //Inicializa un contador a 0
        var contador = 0

        //Encriptar
        if (encriptar) {

            //Bucle for que recorre la lista
            for (i in cargaTabla) {

                //Menor que el tamaño de la cadena
                if(contador < cadenaEntrada.length) {

                    val caracter = cadenaEntrada.substring(contador, contador + 1)

                    val caracter2 = searchASCII(caracter)

                    val resultado = i + caracter2

                    var resultado2 = resultado

                    //Si es mayor o igual al tamaño de la lista
                    if( resultado >= cargaTabla2.size ){
                        //Resta al valor el tamaño de la lista
                        resultado2 = resultado - cargaTabla2.size
                    }

                    //Si es menor que 0
                    if( resultado < 0 ){
                        //Suma al valor el tamaño de la lista
                        resultado2 = resultado + cargaTabla2.size
                    }

                    try {

                        //Concatena a la cadena de texto de salida
                        cadenaSalida += cargaTabla2[resultado2]

                    }catch(e: Exception){
                        println(e)
                    }

                //Si es mayor o igual al tamaño de la tabla
                }else{

                    var resultado = cargaTabla[contador]

                    //Si el valor es menor que 0
                    if(resultado < 0){

                        //Se suma 148 al valor
                        resultado += 148

                    }

                    //Se concatena a la cadena de texto de salida
                    cadenaSalida += cargaTabla2[resultado]

                }

                //Incrementa el contador
                contador++
            }

        //Desencriptar
        } else {

            //Bucle for que recorre la lista
            for (i in cargaTabla) {

                //Multiplica por -1
                val numero = i * -1

                //Menor que el tamaño de la cadena
                if(contador < cadenaEntrada.length) {

                    val caracter = cadenaEntrada.substring(contador, contador + 1)
                    val caracter2 = searchASCII(caracter)
                    val resultado = numero + caracter2

                    var resultado2 = resultado

                    //Si es mayor o igual al tamaño de la lista
                    if( resultado >= cargaTabla2.size ){
                        //Resta al valor el tamaño de la lista
                        resultado2 = resultado - cargaTabla2.size
                    }

                    //Si es menor que 0
                    if( resultado < 0 ){
                        //Suma al valor el tamaño de la lista
                        resultado2 = resultado + cargaTabla2.size
                    }

                    try {

                        if(resultado2!=0) {
                            cadenaSalida += cargaTabla2[resultado2]
                        }

                    }catch(e: Exception){
                        println(e)
                    }
                }
                contador++
            }
        }

        return cadenaSalida
    }

    //Función que devuelve el valor númerico de un cáracter ASCII
    private fun searchASCII(caracter: String): Int {

        //TO CHAR
        val caracter2 = caracter.single()

        //Si la tabla contiene el caracter
        if(cargaTabla2.contains(caracter2)){

            //Devuelve el indice del caracter
            return cargaTabla2.indexOf(caracter2)

        }

        return 0

    }

}