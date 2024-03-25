import java.io.File

const val alfabeto = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
const val MENU_PRINCIPAL = 100
const val MENU_DEFINIR_TABULEIRO = 101
const val MENU_DEFINIR_NAVIOS = 102
const val MENU_JOGAR = 103
const val MENU_LER_FICHEIRO = 104
const val MENU_GRAVAR_FICHEIRO = 105
const val SAIR = 106
var numLinhas = -1
var numColunas = -1
var tabuleiroHumano: Array<Array<Char?>> = emptyArray()
var tabuleiroComputador: Array<Array<Char?>> = emptyArray()
var tabuleiroPalpitesDoHumano: Array<Array<Char?>> = emptyArray()
var tabuleiroPalpitesDoComputador: Array<Array<Char?>> = emptyArray()

//TODO Legenda
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

//TODO Legenda
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

//TODO Legenda
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

//TODO ENRICO
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

//TODO Legenda
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

//TODO Legenda
fun criaTabuleiroVazio(numLinhas:Int, numColunas: Int): Array<Array<Char?>>{
    val tabuleiroVazio = Array(numLinhas) { arrayOfNulls<Char>(numColunas) }
    for (linha in 0.. numLinhas-1) {
        for (coluna in 0.. numColunas-1){
            tabuleiroVazio[linha][coluna] =null
        }
    }
    return tabuleiroVazio
}

//TODO Legenda
fun coordenadaContida(tabuleiro:Array<Array<Char?>>,numLinhas:Int, numColunas: Int):Boolean {
    if (numLinhas in 1 ..tabuleiro.size && numColunas in 1 .. tabuleiro.size) {
        return true
    }
    else{
        return false
    }
}

//TODO Legenda
fun limparCoordenadasVazias(arrayDePairs:Array<Pair<Int,Int>>):Array<Pair<Int,Int>>{ //Todo arrumada e funcionaso falta mandar no drop
    var contagem = 0
    var count = 0
    for(elemento in 0 .. arrayDePairs.size-1){ //verifica quantas cordenadas vazias tem
        if(arrayDePairs[elemento].first != 0 && arrayDePairs[elemento].second!=0){
            contagem ++
        }
    }

    val cordenadas = Array(contagem){Pair(0,0)} //inicializo o array de retorno com o tamnaho de cordenadas nao vazias

    for(elemento in 0 .. arrayDePairs.size-1){ // roda todas as cordenadas
        if(arrayDePairs[elemento].first != 0 && arrayDePairs[elemento].second!=0){ // se nao for vazio ira colocar no array de retorno
            cordenadas[count] = arrayDePairs[elemento]
            count ++
        }
    }
    return cordenadas
}

//TODO Legenda
fun juntarCoordenadas(array:Array<Pair<Int,Int>>,pair:Array<Pair<Int,Int>>):Array<Pair<Int,Int>>{//Todo arrumada e funcionaso falta mandar no drop
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

//Gera as coordenadas do navio, mas se o navio não estiver dentro do tabuleiro retorna um array vazio
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

//Está função gera um array de pairs que consiste na fronteira a volta do navio que está dentro to tabuleiro
fun gerarCoordenadasFronteira(tabuleiro:Array<Array<Char?>>,linha: Int,coluna: Int,orientacao:String,dimensao:Int): Array<Pair<Int,Int>> {
    var fronteira = emptyArray<Pair<Int,Int>>()
    if (dimensao == 1) { //subarino
        for (linhas in linha -1 .. linha + 1) {
            for (colunas in coluna - 1 .. coluna + dimensao) {
                if (!(linhas == linha && colunas == coluna)) { // se a coordenada for difrente da posição do navio
                    if (coordenadaContida(tabuleiro,linhas,colunas)) fronteira += Pair(linhas,colunas)
                    // insere no array a coordenada se tiver contida no tabuleiro
                }
            }
        }
    } else {
        if (orientacao == "E") { // Navios Compostos direção "E"
            for (linhas in linha - 1..linha + 1) {
                for (colunas in coluna - 1..coluna + dimensao) {
                    if (!(linhas == linha && (colunas in coluna..coluna + dimensao - 1))) {
                        // se a coordenada for difrente da posição do navio
                        if (coordenadaContida(tabuleiro,linhas,colunas)) fronteira += Pair(linhas,colunas)
                    // insere no array a coordenada se tiver contida no tabuleiro
                    }
                }
            }
        } else if (orientacao == "O") { // Navios Compostos direção "O"
            for (linhas in linha - 1..linha + 1) {
                for (colunas in coluna - dimensao..coluna + 1) {
                    if (!(linhas == linha && (colunas in coluna - dimensao + 1..coluna))) {
                        if (coordenadaContida(tabuleiro,linhas,colunas)) fronteira += Pair(linhas,colunas)
                    }
                }
            }
        } else if (orientacao == "S") { // Navios Compostos direção "S"
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

//verifica se as cordenadas estao livre
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

// Verifica se é possivel inserir o navio e se for possivel insere o no tabuleiro (funciona apenas direção "E") // TODO VER ERRO
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

// Verifica se é possivel inserir o navio e se for possivel insere o no tabuleiro // TODO VER ERRO
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

//TODO Enrico acabar
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

fun navioCompleto(tabuleiro: Array<Array<Char?>>, linha: Int, coluna: Int): Boolean {
    val tipoDeBarco = tabuleiro[linha-1][coluna-1].toString().toIntOrNull()
    var contagem = 0
    //se na propria cordenada nao for igual ao tipo de barco ja retorna falso
    if (tabuleiro[linha-1][coluna-1].toString() != tipoDeBarco.toString()){
        return false
    }
    //se for 1 simplesmente e completo
    if (tabuleiro[linha-1][coluna-1]=='1'){
        return true
    }
    // para verificar qual o tamanho de navio que tem que ser procurado
    for (tamanho in 1 ..4) {
        // se o tamanho verificar o tipo de barco que estamos procurando ele continua
        if (tamanho == tipoDeBarco) {
            //ve as linhas do tabuleiro referente a posicao inicial para cima e para baixo tamnho -1
            for (linhaTab in linha-(tamanho-1)..linha+(tamanho-1)) {
                // verifica se a cordenada esta no tabuleiro
                if (coordenadaContida(tabuleiro, linhaTab, coluna)) {
                    //se nas cordenadas tiver o velor do tipo de barco prosegue
                    if (tabuleiro[linhaTab - 1][coluna - 1].toString() == tipoDeBarco.toString()) {
                        contagem++
                    }
                }
                //se a contagem for do tamanho do barco ja retorna true
                if (contagem == tipoDeBarco) {
                    return true
                }
            }
            contagem=0
            //ve as colunas do tabuleiro referente a posicao inicial da esquerda para a direita tamnho -1
            for (colunaTab in coluna-(tamanho-1)..coluna+(tamanho-1)) {
                // se a cordenada estiver no tabuleiro e as cordenas serem diferentes a cordenadas iniciais prosegue
                if (coordenadaContida(tabuleiro,linha,colunaTab)) {
                    //se nas cordenadas tiver o velor do tipo de barco prosegue
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


// Se o boolean recebido for true retorna uma representação tabuleiro real e se for false retorna uma representação tabuleiro palpites
fun obtemMapa(tabuleiroEscolhido: Array<Array<Char?>>, tipoDeTabuleiro: Boolean): Array<String> {
    val mapa = Array(tabuleiroEscolhido.size+1) { "" }
    if (tipoDeTabuleiro==false) {
        for (linha in 0 .. tabuleiroEscolhido.size) {
            if (linha == 0) {
                mapa[linha] = "| ${criaLegendaHorizontal(tabuleiroEscolhido[0].size)} |"
            } else {
                var linhaTexto = ""
                for (coluna in 0 ..tabuleiroEscolhido.size-1) {
                    when {
                        tabuleiroEscolhido[linha-1][coluna] == null -> linhaTexto += "| ? "
                        tabuleiroEscolhido[linha-1][coluna] == 'X' -> linhaTexto += "| X "
                        tabuleiroEscolhido[linha-1][coluna] == '2' && !navioCompleto(tabuleiroEscolhido,linha, coluna+1)  -> linhaTexto += "| \u2082 "
                        tabuleiroEscolhido[linha-1][coluna] == '3' && !navioCompleto(tabuleiroEscolhido,linha, coluna+1)  -> linhaTexto += "| \u2083 "
                        tabuleiroEscolhido[linha-1][coluna] == '4' && !navioCompleto(tabuleiroEscolhido,linha, coluna+1)  -> linhaTexto += "| \u2084 "
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
    } else {
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

//TODO ENRICO
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

//TODO ENRICO
fun geraTiroComputador(tabuleiroPalpitesComputador:Array<Array<Char?>>):Pair<Int,Int>{
    var tiroComputador = Pair(0,0)
    do {
        val linha = (1..tabuleiroPalpitesComputador.size).random()
        val coluna = (1..tabuleiroPalpitesComputador.size).random()
        tiroComputador = Pair (linha,coluna)
    }while (tabuleiroPalpitesComputador[linha-1][coluna-1] != null)
    return tiroComputador
}

//TODO feito ENRICO
fun contarNaviosDeDimensao(tabuleiro: Array<Array<Char?>>, dimensao: Int): Int {
    // o tipo de barco
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
    //verifica todas as linhas
    for (linha in 0 .. tabuleiro.size-1) {
        //verifica todas as colunas
        for (coluna in 0 .. tabuleiro.size-1) {
            //se na linha e na coluna e igual ao tipo de barco
            if (tabuleiro[linha][coluna].toString() == tipoBarco) {
                //verifica nas linhas e colunas dadas se o navio esta completo
                if (navioCompleto(tabuleiro, linha+1, coluna+1)) {
                    contagem++
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

fun venceu(tabuleiroDePalpites:Array<Array<Char?>>):Boolean{
    //quais os navios do tabuleiro
    val arrayDeNavios = calculaNumNavios(tabuleiroDePalpites.size,tabuleiroDePalpites.size)
    //total de barcos
    val totalDeBarcos = arrayDeNavios.sum()
    //count
    var contagem = 0
    for (tamanho in 1 .. 4 ){
        if(contarNaviosDeDimensao(tabuleiroDePalpites,tamanho)==arrayDeNavios[tamanho-1]){
            contagem ++
        }
    }
    if (contagem == 4){
        return true
    }
    return false
}
fun lerJogo(nomeDoFicheiro: String, tipoDeTabuleiro: Int): Array<Array<Char?>> {
    val ficheiro = File(nomeDoFicheiro).readLines().toTypedArray()
    val tamanho: Int = if (ficheiro[0][0] == '1') {
        (ficheiro[0][0].toString() + ficheiro[0][1]).toInt()
    } else {
        ficheiro[0][0].toString().toInt()
    }
    val tabuleiro: Array<Array<Char?>> = Array(tamanho) { Array(tamanho) {null} } // tabuleiro de nulls com as dimensões corretas
    when (tipoDeTabuleiro) { // criar tabubleiro baseado no que está no ficheiro
        1 -> { // tabuleiroHumano
            for (linha in 4 until 3 + tamanho+1) {
                val partes = ficheiro[linha].split(",")
                for (coluna in 0 until tamanho) {
                    if (partes[coluna] != "") {
                        tabuleiro[linha - 4][coluna] = partes[coluna][0].toChar() // escreve no tabuleiro o que está no ficheiro
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

//grava o estado de um jogo em um arquivo
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

//TODO Legenda
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

//TODO Legenda
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

//TODO Legenda
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
                                0 -> {
                                    if (insereNavioSimples(tabuleiroHumano,coordenadasReais.first,coordenadasReais.second,1)) {
                                        insereNavioSimples(tabuleiroHumano,coordenadasReais.first,coordenadasReais.second,1)
                                        val mapa = obtemMapa(tabuleiroHumano,true)
                                        for (linha in 0 until mapa.size) {println(mapa[linha])}
                                        navios[percorrerNavios] = navios[percorrerNavios] - 1
                                    }
                                }
                                else -> {
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
    val arrInt = Array<Int>(1) { 1 }
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

//TODO Legenda
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
                        null -> println()
                        else -> {
                            val textos = arrayOf("Agua.","Navio ao fundo!")
                            lancarTiro(tabuleiroComputador,tabuleiroPalpitesDoHumano,tiroReal)
                            navioCompleto(tabuleiroComputador,tiroReal.first,tiroReal.second)
                            var ondeAcertou = ""
                            when {
                                navioCompleto(tabuleiroPalpitesDoHumano,tiroReal.first,tiroReal.second) -> {
                                    ondeAcertou += " ${textos[1]}"
                                }
                            }
                            println(">>> HUMANO >>>${lancarTiro(tabuleiroComputador,tabuleiroPalpitesDoHumano,tiroReal)}$ondeAcertou")
                            val tiroComputador = geraTiroComputador(tabuleiroPalpitesDoComputador)
                            if (!venceu(tabuleiroPalpitesDoHumano)) {
                                lancarTiro(tabuleiroHumano,tabuleiroPalpitesDoComputador,Pair(tiroComputador.first,tiroComputador.second))
                                var ondeAcertouComputador = ""
                                when {
                                    navioCompleto(tabuleiroPalpitesDoComputador,tiroComputador.first,tiroComputador.second) -> {
                                        ondeAcertouComputador += " ${textos[1]}"
                                    }
                                }
                                println("Computador lancou tiro para a posicao (${tiroComputador.first},${tiroComputador.second})\n" +
                                        ">>> COMPUTADOR >>>${lancarTiro(tabuleiroHumano,tabuleiroPalpitesDoComputador,
                                            Pair(tiroComputador.first,tiroComputador.second))}$ondeAcertouComputador")
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

//TODO Legenda
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

//TODO Legenda
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