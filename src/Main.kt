import java.io.File

/** The alphabet string used for column coordinates. */
const val alfabeto = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

/** Constants representing the different menu states in the game. */
const val MENU_PRINCIPAL = 100
const val MENU_DEFINIR_TABULEIRO = 101
const val MENU_DEFINIR_NAVIOS = 102
const val MENU_JOGAR = 103
const val MENU_LER_FICHEIRO = 104
const val MENU_GRAVAR_FICHEIRO = 105
const val SAIR = 106

/** Global variables holding the game state, including board dimensions and the four game boards. */
var numLinhas = -1
var numColunas = -1
var tabuleiroHumano: Array<Array<Char?>> = emptyArray()
var tabuleiroComputador: Array<Array<Char?>> = emptyArray()
var tabuleiroPalpitesDoHumano: Array<Array<Char?>> = emptyArray()
var tabuleiroPalpitesDoComputador: Array<Array<Char?>> = emptyArray()

/**
 * Checks if the given board dimensions are valid for the game.
 *
 * @param numLinhas The number of rows.
 * @param numColunas The number of columns.
 * @return True if the dimensions are one of the allowed sizes (4x4, 5x5, 7x7, 8x8, 10x10), false otherwise.
 */
fun tamanhoTabuleiroValido(numLinhas:Int, numColunas: Int): Boolean {
    return when {
        numLinhas == 4 && numColunas == 4 -> true
        numLinhas == 5 && numColunas == 5 -> true
        numLinhas == 7 && numColunas == 7 -> true
        numLinhas == 8 && numColunas == 8 -> true
        numLinhas == 10 && numColunas == 10 -> true
        else -> false
    }
}

/**
 * Parses a string coordinate (e.g., "3,G" or "10,A") into a Pair of (row, column) indices.
 *
 * @param cordenadas The coordinate string.
 * @param numLinhas The total number of rows on the board (for validation).
 * @param numColunas The total number of columns on the board (for validation).
 * @return A Pair(row, column) as 1-based indices, or null if the format is invalid or out of bounds.
 */
fun processaCoordenadas(cordenadas: String, numLinhas:Int, numColunas:Int):Pair<Int,Int>? {
    if (cordenadas.length == 3 || cordenadas.length == 4 && numLinhas in (1..26) && numColunas in(1..26)){
        var numeroValidacao = cordenadas[0].toString().toIntOrNull()
        var letraCordenada = cordenadas[2].toString()
        var contagem = 0
        if(numeroValidacao==null){
            return null
        }
        if(cordenadas.length == 4){
            numeroValidacao = (cordenadas[0].toString()+cordenadas[1].toString()).toIntOrNull()
            if(numeroValidacao==null ){
                return null
            }
            else{
                letraCordenada = cordenadas[3].toString()
            }
        }
        if (numeroValidacao <= numLinhas && numeroValidacao != 0) {
            while (contagem != numColunas) {
                if (letraCordenada == alfabeto[contagem].toString()) {
                    return Pair(numeroValidacao,contagem+1)
                } else {
                    contagem++
                }
            }
        }
    }
    return null
}

/**
 * Creates the horizontal header string (e.g., "A | B | C") for the game board.
 *
 * @param numColunas The total number of columns.
 * @return The formatted header string.
 */
fun criaLegendaHorizontal(numColunas: Int): String {
    var count = 0
    var sequencia = ""
    while (count < numColunas) {
        sequencia += alfabeto[count].toString()
        if (count < numColunas -1) {
            sequencia += " | "
        }
        count++
    }
    return sequencia
}

/**
 * Generates a string representation of an empty game board (terrain) with row and column headers.
 * (Note: This function appears to be unused in the final game loop; `obtemMapa` is used instead).
 *
 * @param numLinhas The number of rows.
 * @param numColunas The number of columns.
 * @return A multi-line string representing the empty board.
 */
fun criaTerreno(numLinhas:Int, numColunas: Int): String {
    var countNumLinhas = 0
    var sequenciaNumLinhas = "| ${criaLegendaHorizontal(numColunas)} |\n"
    while (countNumLinhas < numLinhas){
        var countLinha = 0
        var sequenciaLinha = ""
        while (countLinha < numColunas) {
            if (countLinha == 0) {
                sequenciaLinha += "| "
            }
            sequenciaLinha += "~ | "
            countLinha++
        }
        sequenciaLinha += "${countNumLinhas + 1}"
        sequenciaNumLinhas += "$sequenciaLinha\n"
        countNumLinhas++
    }
    return  sequenciaNumLinhas
}

/**
 * Determines the number of ships of each type based on the board's dimensions.
 *
 * @param numLinhas The number of rows.
 * @param numColunas The number of columns.
 * @return An array [submarines, destroyers, tankers, carriers]. Returns an empty array if dimensions are invalid.
 */
fun calculaNumNavios(numLinhas:Int, numColunas: Int): Array<Int>{
    return when {
        numLinhas == 4 && numColunas == 4 ->arrayOf(2,0,0,0)
        numLinhas == 5 && numColunas == 5 ->arrayOf(1,1,1,0)
        numLinhas == 7 && numColunas == 7 ->arrayOf(2,1,1,1)
        numLinhas == 8 && numColunas == 8 ->arrayOf(2,2,1,1)
        numLinhas == 10 && numColunas == 10 ->arrayOf(3,2,1,1)
        else-> emptyArray<Int>()
    }
}

/**
 * Creates a new, empty game board (a 2D array) of the given dimensions, filled with nulls.
 *
 * @param numLinhas The number of rows.
 * @param numColunas The number of columns.
 * @return A 2D array of `Char?` initialized to null.
 */
fun criaTabuleiroVazio(numLinhas:Int, numColunas: Int): Array<Array<Char?>>{
    val tabuleiroVazio = Array(numLinhas) { arrayOfNulls<Char>(numColunas) }
    for (linha in 0.. numLinhas-1) {
        for (coluna in 0.. numColunas-1){
            tabuleiroVazio[linha][coluna] =null
        }
    }
    return tabuleiroVazio
}

/**
 * Checks if a given (row, column) coordinate (1-based) is within the bounds of the board.
 *
 * @param tabuleiro The game board array.
 * @param numLinhas The row to check (1-based).
 * @param numColunas The column to check (1-based).
 * @return True if the coordinate is on the board, false otherwise.
 */
fun coordenadaContida(tabuleiro:Array<Array<Char?>>,numLinhas:Int, numColunas: Int):Boolean {
    if (numLinhas in 1 ..tabuleiro.size && numColunas in 1 .. tabuleiro.size) {
        return true
    }
    else{
        return false
    }
}

/**
 * Filters an array of coordinate pairs, returning a new array containing only the pairs that are not (0,0).
 *
 * @param arrayDePairs The input array of coordinate pairs, which may contain (0,0) placeholders.
 * @return A new array containing only the valid coordinates.
 */
fun limparCoordenadasVazias(arrayDePairs:Array<Pair<Int,Int>>):Array<Pair<Int,Int>>{
    var contagem = 0
    var count = 0
    for(elemento in 0 .. arrayDePairs.size-1){ // Count non-empty coordinates
        if(arrayDePairs[elemento].first != 0 && arrayDePairs[elemento].second!=0){
            contagem ++
        }
    }

    val cordenadas = Array(contagem){Pair(0,0)} // Initialize return array with the correct size

    for(elemento in 0 .. arrayDePairs.size-1){ // Populate the return array
        if(arrayDePairs[elemento].first != 0 && arrayDePairs[elemento].second!=0){ // If not empty, add to return array
            cordenadas[count] = arrayDePairs[elemento]
            count ++
        }
    }
    return cordenadas
}

/**
 * Merges two arrays of coordinate pairs into a single new array.
 *
 * @param array The first array of pairs.
 * @param pair The second array of pairs.
 * @return A new array containing all elements from both input arrays.
 */
fun juntarCoordenadas(array:Array<Pair<Int,Int>>,pair:Array<Pair<Int,Int>>):Array<Pair<Int,Int>>{
    val arraySomado = Array(array.size+pair.size){Pair(0,0)}
    var contagem =0
    for (elemto in 0 ..array.size -1){
        arraySomado[contagem]= array[elemto]
        contagem++
    }
    for(elemento in 0..pair.size-1){
        arraySomado[contagem] = pair[elemento]
        contagem ++
    }
    return arraySomado
}

/**
 * Generates an array of (row, column) pairs for a ship based on its start position, orientation, and dimension.
 *
 * @param tabuleiro The game board (used for bounds checking).
 * @param linha The starting row (1-based).
 * @param coluna The starting column (1-based).
 * @param orientacao The orientation ("E", "O", "N", "S").
 * @param dimensao The length of the ship.
 * @return An array of coordinate pairs for the ship, or an empty array if the ship goes out of bounds.
 */
fun gerarCoordenadasNavio(tabuleiro:Array<Array<Char?>>,linha: Int,coluna: Int,orientacao:String,dimensao:Int): Array<Pair<Int,Int>> {
    val coordenadasNavio=Array<Pair<Int,Int>>(dimensao){Pair(0,0)}
    val empty = emptyArray<Pair<Int,Int>>()
    var contagem = 0
    var linhas = linha
    var colunas = coluna
    coordenadasNavio[contagem] = Pair(linhas,colunas)
    while (contagem < dimensao-1){
        contagem ++
        when {
            orientacao =="E"-> {
                colunas++
                if (colunas in 1..tabuleiro.size) {
                    coordenadasNavio[contagem] = Pair(linhas,colunas)
                } else { return empty }
            }
            orientacao =="O"->{
                colunas --
                if (colunas in 1..tabuleiro.size) {
                    coordenadasNavio[contagem] = Pair(linhas,colunas)
                } else { return empty }
            }
            orientacao =="N"->{
                linhas --
                if (linhas in 1..tabuleiro.size) {
                    coordenadasNavio[contagem] = Pair(linhas,colunas)
                } else { return empty }
            }
            orientacao =="S"->{
                linhas ++
                if (linhas in 1..tabuleiro.size) {
                    coordenadasNavio[contagem] = Pair(linhas,colunas)
                } else { return empty }
            }
        }
    }
    return coordenadasNavio
}

/**
 * Generates an array of (row, column) pairs representing the 1-tile border/frontier around a ship's potential location.
 * This is used to prevent ships from touching.
 *
 * @param tabuleiro The game board (used for bounds checking).
 * @param linha The starting row (1-based).
 * @param coluna The starting column (1-based).
 * @param orientacao The ship's orientation.
 * @param dimensao The ship's dimension.
 * @return An array of coordinate pairs for the border, filtered to be within the board.
 */
fun gerarCoordenadasFronteira(tabuleiro:Array<Array<Char?>>,linha: Int,coluna: Int,orientacao:String,dimensao:Int): Array<Pair<Int,Int>> {
    var fronteira = emptyArray<Pair<Int,Int>>()
    if (dimensao == 1) { // Submarine
        for (linhas in linha -1 .. linha + 1) {
            for (colunas in coluna - 1 .. coluna + dimensao) {
                if (!(linhas == linha && colunas == coluna)) { // If coordinate is not the ship's position
                    if (coordenadaContida(tabuleiro,linhas,colunas)) fronteira += Pair(linhas,colunas)
                    // Add coordinate to array if it's on the board
                }
            }
        }
    } else {
        if (orientacao == "E") { // Composite ships, "E" direction
            for (linhas in linha - 1..linha + 1) {
                for (colunas in coluna - 1..coluna + dimensao) {
                    if (!(linhas == linha && (colunas in coluna..coluna + dimensao - 1))) {
                        // If coordinate is not the ship's position
                        if (coordenadaContida(tabuleiro,linhas,colunas)) fronteira += Pair(linhas,colunas)
                        // Add coordinate to array if it's on the board
                    }
                }
            }
        } else if (orientacao == "O") { // Composite ships, "O" direction
            for (linhas in linha - 1..linha + 1) {
                for (colunas in coluna - dimensao..coluna + 1) {
                    if (!(linhas == linha && (colunas in coluna - dimensao + 1..coluna))) {
                        if (coordenadaContida(tabuleiro,linhas,colunas)) fronteira += Pair(linhas,colunas)
                    }
                }
            }
        } else if (orientacao == "S") { // Composite ships, "S" direction
            for (linhas in linha - 1..linha + dimensao) {
                for (colunas in coluna - 1..coluna + 1) {
                    if (!(colunas == coluna && (linhas in linha..linha + dimensao - 1))) {
                        if (coordenadaContida(tabuleiro,linhas,colunas)) fronteira += Pair(linhas,colunas)
                    }
                }
            }
        } else if (orientacao == "N") {
            for (linhas in linha - dimensao..linha + 1) {
                for (colunas in coluna - 1..coluna + 1) {
                    if (!(colunas == coluna && (linhas in linha - dimensao + 1..linha))) {
                        if (coordenadaContida(tabuleiro,linhas,colunas)) fronteira += Pair(linhas,colunas)
                    }
                }
            }
        }
    }
    limparCoordenadasVazias(fronteira)
    return fronteira
}

/**
 * Checks if all coordinates in the given array are empty (null) and within bounds on the game board.
 *
 * @param tabuleiro The game board.
 * @param arrayDeCoordenadas The array of coordinate pairs to check.
 * @return True if all positions are free and valid, false otherwise.
 */
fun estaLivre(tabuleiro:Array<Array<Char?>>,arrayDeCoordenadas:Array<Pair<Int,Int>>):Boolean {
    for (elemento in 0..arrayDeCoordenadas.size-1){
        if(!coordenadaContida(tabuleiro,arrayDeCoordenadas[elemento].first,arrayDeCoordenadas[elemento].second)){
            return false
        }
        if (tabuleiro[arrayDeCoordenadas[elemento].first-1][arrayDeCoordenadas[elemento].second-1] != null){
            return false
        }
    }
    return true
}

/**
 * Attempts to insert a ship with a fixed "E" (East) orientation.
 * Checks for valid placement (ship and frontier) and inserts it.
 *
 * @param tabuleiro The game board to modify.
 * @param linha The starting row (1-based).
 * @param coluna The starting column (1-based).
 * @param dimensao The ship's length.
 * @return True if the ship was successfully placed, false otherwise.
 */
fun insereNavioSimples(tabuleiro: Array<Array<Char?>>, linha: Int, coluna: Int, dimensao: Int): Boolean {
    val navio = gerarCoordenadasNavio(tabuleiro, linha, coluna,"E", dimensao)
    val fronteira = gerarCoordenadasFronteira(tabuleiro, linha, coluna,"E", dimensao)
    val coordenadas = juntarCoordenadas(navio, fronteira)
    val tipoDeBarco = dimensao.toString()[0]
    if (estaLivre(tabuleiro, coordenadas)== true && navio.size!=0) {
        for (contador in 0 until navio.size) {
            tabuleiro[navio[contador].first -1][navio[contador].second -1] = tipoDeBarco
        }
        return true
    }
    return false
}

/**
 * Attempts to insert a ship with a given orientation.
 * Checks for valid placement (ship and frontier) and inserts it.
 *
 * @param tabuleiro The game board to modify.
 * @param linha The starting row (1-based).
 * @param coluna The starting column (1-based).
 * @param orientacao The ship's orientation ("N", "S", "E", "O").
 * @param dimensao The ship's length.
 * @return True if the ship was successfully placed, false otherwise.
 */
fun insereNavio(tabuleiro: Array<Array<Char?>>, linha: Int, coluna: Int, orientacao: String, dimensao: Int): Boolean {
    val navio = gerarCoordenadasNavio(tabuleiro, linha, coluna, orientacao, dimensao)
    val fronteira = gerarCoordenadasFronteira(tabuleiro, linha, coluna, orientacao, dimensao)
    val coordenadas = juntarCoordenadas(navio, fronteira)
    val tipoDeBarco = dimensao.toString()[0]
    if (estaLivre(tabuleiro, coordenadas)== true && navio.size!=0) {
        for (contador in 0 until navio.size) {
            tabuleiro[navio[contador].first -1][navio[contador].second -1] = tipoDeBarco
        }
        return true
    }
    return false
}

/**
 * Populates the computer's game board with a predefined, hardcoded layout of ships based on the board size.
 *
 * @param tabuleiroVazio An empty board to be filled.
 * @param dimensao An array of ship counts (this parameter seems unused, the logic is hardcoded by board size).
 */
fun preencheTabuleiroComputador(tabuleiroVazio: Array<Array<Char?>>, dimensao: Array<Int>) {
    val tabuleiro = tabuleiroVazio
    if (tabuleiro.size == 4) {
        tabuleiro[0][0] = '1'
        tabuleiro[3][3] = '1'
    } else if (tabuleiro.size == 5) {
        tabuleiro[0][0] = '1'
        tabuleiro[4][0] = '2'
        tabuleiro[4][1] = '2'
        tabuleiro[2][2] = '3'
        tabuleiro[2][3] = '3'
        tabuleiro[2][4] = '3'
    } else if (tabuleiro.size == 7) {
        tabuleiro[0][0] = '1'
        tabuleiro[0][6] = '1'
        tabuleiro[2][0] = '2'
        tabuleiro[2][1] = '2'
        tabuleiro[2][4] = '3'
        tabuleiro[2][5] = '3'
        tabuleiro[2][6] = '3'
        tabuleiro[4][0] = '4'
        tabuleiro[4][1] = '4'
        tabuleiro[4][2] = '4'
        tabuleiro[4][3] = '4'
    }
    else if (tabuleiro.size == 8) {
        tabuleiro[0][0] = '1'
        tabuleiro[0][7] = '1'
        tabuleiro[0][3] = '2'
        tabuleiro[0][4] = '2'
        tabuleiro[2][0] = '2'
        tabuleiro[2][1] = '2'
        tabuleiro[3][4] = '3'
        tabuleiro[3][5] = '3'
        tabuleiro[3][6] = '3'
        tabuleiro[6][1] = '4'
        tabuleiro[6][2] = '4'
        tabuleiro[6][3] = '4'
        tabuleiro[6][4] = '4'
    }
    else if (tabuleiro.size == 10) {
        tabuleiro[0][0] = '1'
        tabuleiro[0][7] = '1'
        tabuleiro[8][7] = '1'
        tabuleiro[0][3] = '2'
        tabuleiro[0][4] = '2'
        tabuleiro[2][0] = '2'
        tabuleiro[2][1] = '2'
        tabuleiro[3][4] = '3'
        tabuleiro[3][5] = '3'
        tabuleiro[3][6] = '3'
        tabuleiro[6][1] = '4'
        tabuleiro[6][2] = '4'
        tabuleiro[6][3] = '4'
        tabuleiro[6][4] = '4'
    }
    tabuleiroComputador = tabuleiro
}

/**
 * Checks if the ship part at a given (row, column) on a *guess board* is part of a completely sunk ship.
 *
 * @param tabuleiro The guess board.
 * @param linha The row of the ship part (1-based).
 * @param coluna The column of the ship part (1-based).
 * @return True if the entire ship is revealed (sunk), false otherwise.
 */
fun navioCompleto(tabuleiro: Array<Array<Char?>>, linha: Int, coluna: Int): Boolean {
    val tipoDeBarco = tabuleiro[linha-1][coluna-1].toString().toIntOrNull()
    var contagem = 0
    // If the coordinate isn't a ship part, return false
    if (tabuleiro[linha-1][coluna-1].toString() != tipoDeBarco.toString()){
        return false
    }
    // If it's a submarine ('1'), it's always complete
    if (tabuleiro[linha-1][coluna-1]=='1'){
        return true
    }
    // Check for other ship types
    for (tamanho in 1 ..4) {
        // If the size matches the ship type we're looking for
        if (tamanho == tipoDeBarco) {
            // Check vertically
            for (linhaTab in linha-(tamanho-1)..linha+(tamanho-1)) {
                if (coordenadaContida(tabuleiro, linhaTab, coluna)) {
                    if (tabuleiro[linhaTab - 1][coluna - 1].toString() == tipoDeBarco.toString()) {
                        contagem++
                    }
                }
                if (contagem == tipoDeBarco) {
                    return true
                }
            }
            contagem=0
            // Check horizontally
            for (colunaTab in coluna-(tamanho-1)..coluna+(tamanho-1)) {
                if (coordenadaContida(tabuleiro,linha,colunaTab)) {
                    if (tabuleiro[linha - 1][colunaTab-1].toString() == tipoDeBarco.toString()) {
                        contagem++
                    }
                }
                if ( contagem == tipoDeBarco){
                    return true
                }
            }
        }
    }
    return false
}


/**
 * Generates a string array for printing the game board.
 *
 * @param tabuleiroEscolhido The board to display (either real or guess).
 * @param tipoDeTabuleiro `true` shows the real board (with '~' for water).
 * `false` shows the guess board (with '?', 'X', and special chars for partial/sunk ships).
 * @return An array of strings, where each string is a printable row of the board.
 */
fun obtemMapa(tabuleiroEscolhido: Array<Array<Char?>>, tipoDeTabuleiro: Boolean): Array<String> {
    val mapa = Array(tabuleiroEscolhido.size+1) { "" }
    if (tipoDeTabuleiro==false) { // Guess board
        for (linha in 0 .. tabuleiroEscolhido.size) {
            if (linha == 0) {
                mapa[linha] = "| ${criaLegendaHorizontal(tabuleiroEscolhido[0].size)} |"
            } else {
                var linhaTexto = ""
                for (coluna in 0 ..tabuleiroEscolhido.size-1) {
                    when {
                        tabuleiroEscolhido[linha-1][coluna] == null -> linhaTexto += "| ? "
                        tabuleiroEscolhido[linha-1][coluna] == 'X' -> linhaTexto += "| X "
                        tabuleiroEscolhido[linha-1][coluna] == '2' && !navioCompleto(tabuleiroEscolhido,linha, coluna+1)  -> linhaTexto += "| \u2082 " // Subscript 2
                        tabuleiroEscolhido[linha-1][coluna] == '3' && !navioCompleto(tabuleiroEscolhido,linha, coluna+1)  -> linhaTexto += "| \u2083 " // Subscript 3
                        tabuleiroEscolhido[linha-1][coluna] == '4' && !navioCompleto(tabuleiroEscolhido,linha, coluna+1)  -> linhaTexto += "| \u2084 " // Subscript 4
                        tabuleiroEscolhido[linha-1][coluna] == '1' -> linhaTexto += "| 1 "
                        tabuleiroEscolhido[linha-1][coluna] == '2'&& navioCompleto(tabuleiroEscolhido,linha, coluna+1) -> linhaTexto += "| 2 "
                        tabuleiroEscolhido[linha-1][coluna] == '3' && navioCompleto(tabuleiroEscolhido,linha, coluna+1)-> linhaTexto += "| 3 "
                        tabuleiroEscolhido[linha-1][coluna] == '4'&& navioCompleto(tabuleiroEscolhido,linha, coluna+1) -> linhaTexto += "| 4 "
                    }
                }
                linhaTexto += "| ${linha}"
                mapa[linha] = linhaTexto
            }
        }
    } else { // Real board
        for (linha in 0 until tabuleiroEscolhido.size+1) {
            if (linha == 0) {
                mapa[0] = "| ${criaLegendaHorizontal(tabuleiroEscolhido[0].size)} |"
            } else {
                var linhaTexto = ""
                for (coluna in 0 until tabuleiroEscolhido[linha - 1].size) {
                    when (tabuleiroEscolhido[linha - 1][coluna]) {
                        null -> linhaTexto += "| ~ "
                        'X' -> linhaTexto += "| X "
                        '1' -> linhaTexto += "| 1 "
                        '2' -> linhaTexto += "| 2 "
                        '3' -> linhaTexto += "| 3 "
                        '4' -> linhaTexto += "| 4 "
                    }
                }
                linhaTexto += "| ${linha }"
                mapa[linha] = linhaTexto
            }
        }
    }
    return mapa
}

/**
 * Processes a shot at the given coordinates.
 * It updates the `tabuleiroPalpites` (guess board) based on what's on the `tabuleiroReal` (real board)
 * and returns a message about the result (hit, miss, ship type).
 *
 * @param tabuleiroReal The opponent's real board.
 * @param tabuleiroPalpites The current player's guess board (to be updated).
 * @param coordenadasTiro The (row, column) pair for the shot.
 * @return A string message describing the outcome.
 */
fun lancarTiro(tabuleiroReal:Array<Array<Char?>>, tabuleiroPalpites:Array<Array<Char?>>, coordenadasTiro: Pair<Int,Int>): String{
    val resposta = arrayOf("Tiro num submarino.","Tiro num contra-torpedeiro.","Tiro num navio-tanque.","Tiro num porta-avioes.","Agua.")
    var palpite = 0
    when (tabuleiroReal[coordenadasTiro.first-1][coordenadasTiro.second-1]) {
        '1' -> {
            tabuleiroPalpites[coordenadasTiro.first-1][coordenadasTiro.second-1] = '1'
            palpite = 0
        }
        '2' -> {
            tabuleiroPalpites[coordenadasTiro.first-1][coordenadasTiro.second-1] = '2'
            palpite = 1
        }
        '3' -> {
            tabuleiroPalpites[coordenadasTiro.first-1][coordenadasTiro.second-1] = '3'
            palpite = 2
        }
        '4' -> {
            tabuleiroPalpites[coordenadasTiro.first-1][coordenadasTiro.second-1] = '4'
            palpite = 3
        }
        null -> {
            tabuleiroPalpites[coordenadasTiro.first-1][coordenadasTiro.second-1] = 'X'
            palpite = 4
        }
    }
    return resposta[palpite]
}

/**
 * Generates a random, valid (row, column) pair for the computer's shot,
 * ensuring it hasn't shot at that location before.
 *
 * @param tabuleiroPalpitesComputador The computer's guess board.
 * @return A (row, column) pair for the computer's next shot.
 */
fun geraTiroComputador(tabuleiroPalpitesComputador:Array<Array<Char?>>):Pair<Int,Int>{
    var tiroComputador = Pair(0,0)
    do {
        val linha = (1..tabuleiroPalpitesComputador.size).random()
        val coluna = (1..tabuleiroPalpitesComputador.size).random()
        tiroComputador = Pair (linha,coluna)
    }while (tabuleiroPalpitesComputador[linha-1][coluna-1] != null) // Keep trying until an empty spot (null) is found
    return tiroComputador
}

/**
 * Counts the number of *completely sunk* ships of a specific dimension on a given guess board.
 *
 * @param tabuleiro The guess board.
 * @param dimensao The ship dimension (1, 2, 3, or 4) to count.
 * @return The total count of sunk ships of that dimension.
 */
fun contarNaviosDeDimensao(tabuleiro: Array<Array<Char?>>, dimensao: Int): Int {
    val tipoBarco = dimensao.toString()
    var contagem = 0
    var total = 0
    val todos = calculaNumNavios(tabuleiro.size,tabuleiro.size)
    when (dimensao){
        1->{total = todos[0]}
        2->{total = todos[1]}
        3->{total = todos[2]}
        4->{total = todos[3]}
    }
    // Iterate over the entire board
    for (linha in 0 .. tabuleiro.size-1) {
        for (coluna in 0 .. tabuleiro.size-1) {
            // If the cell matches the ship type
            if (tabuleiro[linha][coluna].toString() == tipoBarco) {
                // Check if the ship it belongs to is completely sunk
                if (navioCompleto(tabuleiro, linha+1, coluna+1)) {
                    contagem++
                    // This logic avoids overcounting parts of the same ship
                    if (contagem == 2*total&& dimensao == 2){
                        contagem = contagem /2
                    }
                    if (contagem == 3*total && dimensao ==3){
                        contagem = contagem /3
                    }
                    if (contagem == 4*total&& dimensao == 4){
                        contagem = contagem /4
                    }
                }
            }
        }
    }
    return contagem
}

/**
 * Checks if a player has won by comparing the number of sunk ships on their
 * guess board against the total required number of ships.
 *
 * @param tabuleiroDePalpites The player's guess board.
 * @return True if all opponent's ships are sunk, false otherwise.
 */
fun venceu(tabuleiroDePalpites:Array<Array<Char?>>):Boolean{
    // Get the required number of ships for this board size
    val arrayDeNavios = calculaNumNavios(tabuleiroDePalpites.size,tabuleiroDePalpites.size)
    // Get total number of ships
    val totalDeBarcos = arrayDeNavios.sum()
    var contagem = 0
    // Count how many ship types have been completely sunk
    for (tamanho in 1 .. 4 ){
        if(contarNaviosDeDimensao(tabuleiroDePalpites,tamanho)==arrayDeNavios[tamanho-1]){
            contagem ++
        }
    }
    // If all 4 types are complete, the player has won
    if (contagem == 4){
        return true
    }
    return false
}

/**
 * Reads one of the four game boards from a specified save file.
 *
 * @param nomeDoFicheiro The name of the file to read.
 * @param tipoDeTabuleiro An integer (1-4) specifying which board to read:
 * 1: tabuleiroHumano
 * 2: tabuleiroPalpitesDoHumano
 * 3: tabuleiroComputador
 * 4: tabuleiroPalpitesDoComputador
 * @return The 2D array (board) read from the file.
 */
fun lerJogo(nomeDoFicheiro: String, tipoDeTabuleiro: Int): Array<Array<Char?>> {
    val ficheiro = File(nomeDoFicheiro).readLines().toTypedArray()
    val tamanho: Int = if (ficheiro[0][0] == '1') {
        (ficheiro[0][0].toString() + ficheiro[0][1]).toInt()
    } else {
        ficheiro[0][0].toString().toInt()
    }
    val tabuleiro: Array<Array<Char?>> = Array(tamanho) { Array(tamanho) {null} } // Empty board of correct size
    when (tipoDeTabuleiro) { // Create board based on file content
        1 -> { // tabuleiroHumano
            for (linha in 4 until 3 + tamanho+1) {
                val partes = ficheiro[linha].split(",")
                for (coluna in 0 until tamanho) {
                    if (partes[coluna] != "") {
                        tabuleiro[linha - 4][coluna] = partes[coluna][0].toChar() // Write to board
                    }
                }
            }
        }
        2 -> { // tabuleiroPalpitesDoHumano
            for (linha in 7 + tamanho until 6 + (tamanho * 2)+1) {
                val partes = ficheiro[linha].split(",")
                for (coluna in 0 until tamanho) {
                    if (partes[coluna] != "") {
                        tabuleiro[linha - (7 + tamanho)][coluna] = partes[coluna][0].toChar()
                    }
                }
            }
        }
        3 -> { // tabuleiroComputador
            for (linha in 10 + (tamanho * 2) until 9 + (tamanho * 3)+1) {
                val partes = ficheiro[linha].split(",")
                for (coluna in 0 until tamanho) {
                    if (partes[coluna] != "") {
                        tabuleiro[linha - (10 + (tamanho * 2))][coluna] = partes[coluna][0].toChar()
                    }
                }
            }
        }
        4 -> { // tabuleiroPalpitesDoComputador
            for (linha in 13 + (tamanho * 3) until 12 + (tamanho * 4)+1) {
                val partes = ficheiro[linha].split(",")
                for (coluna in 0 until tamanho) {
                    if (partes[coluna] != "") {
                        tabuleiro[linha - (13 + (tamanho * 3))][coluna] = partes[coluna][0].toChar()
                    }
                }
            }
        }
    }
    return tabuleiro
}

/**
 * Saves the complete current game state (all four boards) to a text file.
 *
 * @param nomeFicheiro The name of the file to create.
 * @param tabuleiroRealHumano The human's real board.
 * @param tabuleiroPalpitesHumano The human's guess board.
 * @param tabuleiroRealComputador The computer's real board.
 * @param tabuleiroPalpitesComputador The computer's guess board.
 */
fun gravarJogo(nomeFicheiro: String,tabuleiroRealHumano:Array<Array<Char?>>,
               tabuleiroPalpitesHumano:Array<Array<Char?>>,
               tabuleiroRealComputador:Array<Array<Char?>>,
               tabuleiroPalpitesComputador:Array<Array<Char?>>) {
    val printer = File(nomeFicheiro).printWriter()
    printer.println("${tabuleiroRealHumano.size},${tabuleiroRealHumano.size}\n")
    printer.println("Jogador\nReal")
    for (linha in 0 until tabuleiroRealHumano.size) {
        for (coluna in 0 until tabuleiroRealHumano[linha].size) {
            val caracterParaImprimir = tabuleiroRealHumano[linha][coluna]?.toString() ?: ""
            printer.print(caracterParaImprimir)
            if (coluna < tabuleiroRealHumano[linha].size - 1) {
                printer.print(",")
            }
        }
        printer.println()
    }
    printer.println()
    printer.println("Jogador\nPalpites")
    for (linha in 0 until tabuleiroPalpitesHumano.size) {
        for (coluna in 0 until tabuleiroPalpitesHumano[linha].size) {
            val caracterParaImprimir = tabuleiroPalpitesHumano[linha][coluna]?.toString() ?: ""
            printer.print(caracterParaImprimir)
            if (coluna < tabuleiroPalpitesHumano[linha].size - 1) {
                printer.print(",")
            }
        }
        printer.println()
    }
    printer.println()
    printer.println("Computador\nReal")
    for (linha in 0 until tabuleiroRealComputador.size) {
        for (coluna in 0 until tabuleiroRealComputador[linha].size) {
            val caracterParaImprimir = tabuleiroRealComputador[linha][coluna]?.toString() ?: ""
            printer.print(caracterParaImprimir)
            if (coluna < tabuleiroRealComputador[linha].size - 1) {
                printer.print(",")
            }
        }
        printer.println()
    }
    printer.println()
    printer.println("Computador\nPalpites")
    for (linha in 0 until tabuleiroPalpitesComputador.size) {
        for (coluna in 0 until tabuleiroPalpitesComputador[linha].size) {
            val caracterParaImprimir = tabuleiroPalpitesComputador[linha][coluna]?.toString() ?: ""
            printer.print(caracterParaImprimir)
            if (coluna < tabuleiroPalpitesComputador[linha].size - 1) {
                printer.print(",")
            }
        }
        printer.println()
    }
    printer.close()
}

/**
 * Displays the main menu, reads user input, and returns the constant for the selected menu state.
 *
 * @return The integer constant representing the next menu state (e.g., MENU_PRINCIPAL, SAIR).
 */
fun menuPrincipal():Int {
    println("\n> > Batalha Naval < <\n")
    println("1 - Definir Tabuleiro e Navios")
    println("2 - Jogar")
    println("3 - Gravar")
    println("4 - Ler")
    println("0 - Sair\n")
    var menuSelecionado = readln().toIntOrNull()
    if (menuSelecionado !in -1..4) {
        do {
            println("!!! Opcao invalida, tente novamente")
            menuSelecionado = readln().toIntOrNull()
        } while (menuSelecionado !in -1..4)
    }
    return when (menuSelecionado) {
        -1 -> MENU_PRINCIPAL
        1 -> MENU_DEFINIR_TABULEIRO
        2 -> MENU_JOGAR
        3 -> MENU_GRAVAR_FICHEIRO
        4 -> MENU_LER_FICHEIRO
        0 -> SAIR
        else -> SAIR
    }
}

/**
 * Handles the "Define Board" menu. Prompts the user for dimensions, validates them,
 * and initializes the global board variables.
 *
 * @return The next menu state (MENU_DEFINIR_NAVIOS on success, or back to MAIN/SAIR).
 */
fun menuDefinirTabuleiro():Int {
    println("\n> > Batalha Naval < <\n")
    println("Defina o tamanho do tabuleiro:")
    var numeroLinhas: Int? = 0
    var numeroColunas: Int? = 0
    do {
        println("Quantas linhas?")
        numeroLinhas = readln().toIntOrNull()
        if (numeroLinhas == -1) return MENU_PRINCIPAL
        if (numeroLinhas == 0) return SAIR
        if (numeroLinhas == null) {
            println("!!! Número de linhas invalidas, tente novamente")
        }
    } while (numeroLinhas == null)
    do {
        println("Quantas colunas?")
        numeroColunas = readln().toIntOrNull()
        if (numeroColunas == -1) return MENU_PRINCIPAL
        if (numeroColunas == 0) return SAIR
        if (numeroColunas == null) {
            println("!!! Número de Colunas invalidas, tente novamente")
        }
    } while (numeroColunas == null)
    if (tamanhoTabuleiroValido(numeroLinhas,numeroColunas)) {
        numLinhas = numeroLinhas
        numColunas = numeroColunas
        tabuleiroHumano = Array(numLinhas) { Array(numColunas) {null} }
        tabuleiroPalpitesDoHumano = Array(numLinhas) { Array(numColunas) {null} }
        tabuleiroComputador = Array(numLinhas) { Array(numColunas) {null} }
        tabuleiroPalpitesDoComputador = Array(numLinhas) { Array(numColunas) {null} }
        return MENU_DEFINIR_NAVIOS
    }
    return MENU_DEFINIR_TABULEIRO
}

/**
 * Handles the "Define Ships" menu. Guides the human player through placing each of their ships on the board.
 * Also populates the computer's board.
 *
 * @return The next menu state (MAIN or SAIR).
 */
fun menuDefinirNavios(): Int {
    val mapa = obtemMapa(tabuleiroHumano,true)
    for (linha in 0 until mapa.size) {println(mapa[linha])}
    val textos = arrayOf(
        "Insira as coordenadas de um submarino:", "Insira as coordenadas de um contra-torpedeiro:",
        "Insira as coordenadas de um navio-tanque:", "Insira as coordenadas de um porta-aviões:")
    val navios = calculaNumNavios(numLinhas,numColunas)
    for (percorrerNavios in 0 until navios.size) {
        while (navios[percorrerNavios] != 0) {
            println("${textos[percorrerNavios]}\nCoordenadas? (ex: 6,G)")
            val coordenadasInseridas = readln()
            when (coordenadasInseridas) {
                "-1" -> return MENU_PRINCIPAL
                "0" -> return SAIR
                else -> {
                    when (val coordenadasReais = processaCoordenadas(coordenadasInseridas,numLinhas,numColunas)) {
                        null -> {println("!!! Coordenadas invalidas, tente novamente")}
                        else -> {
                            when (percorrerNavios) {
                                0 -> { // Submarine
                                    if (insereNavioSimples(tabuleiroHumano,coordenadasReais.first,coordenadasReais.second,1)) {
                                        insereNavioSimples(tabuleiroHumano,coordenadasReais.first,coordenadasReais.second,1)
                                        val mapa = obtemMapa(tabuleiroHumano,true)
                                        for (linha in 0 until mapa.size) {println(mapa[linha])}
                                        navios[percorrerNavios] = navios[percorrerNavios] - 1
                                    }
                                }
                                else -> { // Other ships
                                    var orientacao = ""
                                    do {
                                        println("Insira a orientacao do navio:\nOrientacao? (N, S, E, O)")
                                        orientacao = readln()
                                        when (orientacao) {
                                            "-1" -> return MENU_PRINCIPAL
                                            "0" -> return SAIR
                                            else -> { if (orientacao != "N" && orientacao != "S" && orientacao != "O" && orientacao != "E") {
                                                println("!!! Orientacao invalida, tente novamente")
                                            }
                                            }
                                        }
                                    } while (orientacao != "N" && orientacao != "S" && orientacao != "O" && orientacao != "E")
                                    if (insereNavio(tabuleiroHumano,coordenadasReais.first,coordenadasReais.second,orientacao,percorrerNavios + 1)) {
                                        insereNavio(tabuleiroHumano,coordenadasReais.first,coordenadasReais.second,orientacao,percorrerNavios + 1)
                                        val mapa = obtemMapa(tabuleiroHumano,true)
                                        for (linha in 0 until mapa.size) {println(mapa[linha])}
                                        navios[percorrerNavios] = navios[percorrerNavios] - 1
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    val arrInt = Array<Int>(1) { 1 } // Dummy array
    preencheTabuleiroComputador(criaTabuleiroVazio(numLinhas,numColunas),arrInt)
    println("Pretende ver o mapa gerado para o Computador? (S/N)")
    val resposta = readln()
    when (resposta) {
        "-1" -> return MENU_PRINCIPAL
        "0" -> return SAIR
        "S" -> { val mapa = obtemMapa(tabuleiroComputador,true)
            for (linha in 0 until mapa.size) {println(mapa[linha])}
            return MENU_PRINCIPAL
        } else -> {return MENU_PRINCIPAL}
    }
}

/**
 * Handles the main "Play Game" loop. Alternates turns between the human and computer,
 * processing shots and displaying results until one player wins.
 *
 * @return The next menu state (MAIN or SAIR).
 */
fun menuJogar():Int {
    if (tabuleiroHumano.size == 0){
        println("!!! Tem que primeiro definir o tabuleiro do jogo, tente novamente")
        return MENU_PRINCIPAL
    } else {
        while (!venceu(tabuleiroPalpitesDoHumano) && !venceu(tabuleiroPalpitesDoComputador)) {
            val mapa = obtemMapa(tabuleiroPalpitesDoHumano,false)
            for (linha in 0 until mapa.size) {
                println(mapa[linha])
            }
            println("Indique a posição que pretende atingir")
            println("Coordenadas? (ex: 6,G)")
            val tiroInserido = readln()
            when (tiroInserido) {
                "-1" -> return MENU_PRINCIPAL
                "0" -> return SAIR
                else -> {
                    val tiroReal = processaCoordenadas(tiroInserido,numLinhas,numColunas)
                    when (tiroReal) {
                        null -> println() // Invalid coordinate, loop repeats
                        else -> {
                            val textos = arrayOf("Agua.","Navio ao fundo!")
                            val resultadoTiro = lancarTiro(tabuleiroComputador,tabuleiroPalpitesDoHumano,tiroReal)
                            var ondeAcertou = ""
                            when {
                                navioCompleto(tabuleiroPalpitesDoHumano,tiroReal.first,tiroReal.second) -> {
                                    ondeAcertou += " ${textos[1]}"
                                }
                            }
                            println(">>> HUMANO >>>${resultadoTiro}$ondeAcertou")

                            if (!venceu(tabuleiroPalpitesDoHumano)) { // Check if human won before computer shoots
                                val tiroComputador = geraTiroComputador(tabuleiroPalpitesDoComputador)
                                val resultadoTiroComputador = lancarTiro(tabuleiroHumano,tabuleiroPalpitesDoComputador,Pair(tiroComputador.first,tiroComputador.second))
                                var ondeAcertouComputador = ""
                                when {
                                    navioCompleto(tabuleiroPalpitesDoComputador,tiroComputador.first,tiroComputador.second) -> {
                                        ondeAcertouComputador += " ${textos[1]}"
                                    }
                                }
                                println("Computador lancou tiro para a posicao (${tiroComputador.first},${alfabeto[tiroComputador.second -1]})\n" +
                                        ">>> COMPUTADOR >>>${resultadoTiroComputador}$ondeAcertouComputador")
                            }
                        }
                    }
                }
            }
            if (!venceu(tabuleiroPalpitesDoHumano) && !venceu(tabuleiroPalpitesDoComputador)) {
                println("Prima enter para continuar")
                val enter = readln()
                when (enter) {
                    "-1" -> MENU_PRINCIPAL
                    "0" -> SAIR
                    else -> print("")
                }
            }
        }
        if (venceu(tabuleiroPalpitesDoHumano)) {
            println("PARABENS! Venceu o jogo!")
        } else {
            println("OPS! O Computador venceu o Jogo!")
        }
    }
    println("Prima enter para voltar ao menu principal")
    val enter = readln()
    return MENU_PRINCIPAL
}

/**
 * Handles the "Read File" menu. Prompts for a filename and loads the
 * game state (all four boards) from it.
 *
 * @return The next menu state (MAIN or SAIR).
 */
fun menuLerFicheiro():Int {
    println("Introduza o nome do ficheiro (ex: jogo.txt)")
    val nomeFicheiro = readln()
    when (nomeFicheiro){
        "-1" -> return MENU_PRINCIPAL
        "0" -> return SAIR
        else -> {
            tabuleiroHumano = lerJogo(nomeFicheiro,1)
            tabuleiroPalpitesDoHumano = lerJogo(nomeFicheiro,2)
            tabuleiroComputador = lerJogo(nomeFicheiro,3)
            tabuleiroPalpitesDoComputador = lerJogo(nomeFicheiro,4)
            val tamanho = tabuleiroHumano.size
            println("Tabuleiro ${tamanho}x${tamanho} lido com sucesso")
            val mapa = obtemMapa(tabuleiroHumano, true)
            for (linha in mapa) {
                println(linha)
            }
            numLinhas = tabuleiroHumano.size
            numColunas = tabuleiroHumano.size
            return MENU_PRINCIPAL
        }
    }
}

/**
 * Handles the "Save File" menu. Prompts for a filename and saves
 * the current game state to it.
 *
 * @return The next menu state (MAIN or SAIR).
 */
fun menuGravarFicheiro():Int {
    if (tabuleiroHumano.size == 0){
        println("!!! Tem que primeiro definir o tabuleiro do jogo, tente novamente")
        return MENU_PRINCIPAL
    }
    println("Introduza o nome do ficheiro (ex: jogo.txt)")
    val nomeFicheiro = readln()
    when (nomeFicheiro){
        "-1" -> return MENU_PRINCIPAL
        "0" -> return SAIR
        else -> {
            gravarJogo(nomeFicheiro,tabuleiroHumano,tabuleiroPalpitesDoHumano,tabuleiroComputador,tabuleiroPalpitesDoComputador)
            println("Tabuleiro ${numLinhas}x${numColunas} gravado com sucesso")
            return MENU_PRINCIPAL
        }
    }
}

/**
 * The main entry point of the application.
 * Runs the menu-driven game loop (state machine).
 */
fun main() {
    var menuActual = MENU_PRINCIPAL
    while (true) {
        menuActual = when (menuActual) {
            MENU_PRINCIPAL -> menuPrincipal()
            MENU_DEFINIR_TABULEIRO -> menuDefinirTabuleiro()
            MENU_DEFINIR_NAVIOS -> menuDefinirNavios()
            MENU_JOGAR -> menuJogar()
            MENU_LER_FICHEIRO -> menuLerFicheiro()
            MENU_GRAVAR_FICHEIRO -> menuGravarFicheiro()
            SAIR -> return
            else -> return
        }
    }
}