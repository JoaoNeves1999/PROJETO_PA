//------------------CLASSES--------------------------------------

abstract class Element{
    abstract fun writeText(): String
    abstract fun accept(v: Visitor)
}

class JSON(val listobj: MutableList<JsonObject>){

    fun writeText(): String {
        var text: String = ""
        listobj.forEach{
            text += it.writeText() + "\n"
        }
        return text
    }

    fun accept(v: Visitor){
        listobj.forEach{
            it.accept(v)
        }
    }
}

class JsonValue(val id: String, val key: Element?): Element() {

    override fun writeText(): String {
        return "\"$id\"" + ":" + " ${key?.writeText()}"
    }

    override fun accept(v: Visitor) {
        v.visit(this)
        key?.accept(v)
    }
}

class JsonObject(var listValues: MutableList<JsonValue>): Element() {

    override fun writeText(): String{
        var text: String = "{\n"
        listValues.forEach{
            if(it == listValues.last()) {
                text += it.writeText() + "\n"
            }else{
                text += it.writeText() + ",\n"
            }
        }
        text += "}"
        return text
    }

    override fun accept(v: Visitor) {
        v.visit(this)
        listValues.forEach{
            it.accept(v)
        }
        v.endVisit(this)
    }

    fun firstVal(): JsonValue{
        return listValues.first()
    }
}

class JsonArray(var arrayListValues: MutableList<JsonValue>): Element(){

    override fun writeText(): String{
        var text: String = "["
        arrayListValues.forEach{
            if(it == arrayListValues.last()) {
                text += "${it.key?.writeText()}"
            }else{
                text += "${it.key?.writeText()}" + ", "
            }
        }
        text += "]"
        return text
    }

    override fun accept(v: Visitor) {
        v.visit(this)
        arrayListValues.forEach{
            it.accept(v)
        }
        v.endVisit(this)
    }

    fun firstVal(): JsonValue{
        return arrayListValues.first()
    }
}

class JsonString(val string: String): Element(){

    override fun writeText(): String{
        return "\"$string\""
    }

    override fun accept(v: Visitor) {}
}

class JsonNumber(val float: Float): Element(){

    override fun writeText(): String{
        return "$float"
    }

    override fun accept(v: Visitor) {}
}

class JsonBoolean(val boolean: Boolean): Element() {

    override fun writeText(): String{
        return "$boolean"
    }

    override fun accept(v: Visitor) {}
}

interface Visitor{
    fun visit(objects: JsonObject) {}
    fun visit(array: JsonArray) {}
    fun visit(value: JsonValue) {}
    fun endVisit(array: JsonArray) {}
    fun endVisit(obj: JsonObject) {}
}


//------------------FUNCTIONS--------------------------------------

fun findStrings(json: JSON): String{
    val v = object: Visitor {
        var text: String = ""
        override fun visit(value: JsonValue){
            if(value.key is JsonString){
                text += value.key.writeText() + "\n"
            }
        }
    }
    json.accept(v)
    return v.text
}

fun findCertain(json: JSON, name: String): String{
    val v = object: Visitor{
        var text = ""
        override fun visit(value: JsonValue){
            if(value.id == name){
                text += value.key?.writeText() + "\n"
            }
        }
    }
    json.accept(v)
    return v.text
}

//------------------------------------MAIN----------------------------------

fun main(){

    var list = mutableListOf<JsonValue>()
    var listarray = mutableListOf<JsonValue>()
    var listarray2 = mutableListOf<JsonValue>()

    //1º Objeto
    val nome = JsonValue("Nome", JsonString("João"))
    val idade = JsonValue("Idade", JsonNumber(22F))
    val existe = JsonValue("Existe", JsonBoolean(true))
    val array = JsonValue("Array", JsonArray(listarray))
    val segundoArray = JsonValue("Segundo Array", JsonArray(listarray2))

    list.add(nome)
    list.add(idade)
    list.add(existe)
    list.add(array)

    listarray.add(nome)
    listarray.add(idade)
    listarray.add(existe)
    listarray.add(segundoArray)

    listarray2.add(nome)

    val joao = JsonObject(list)

    var list2 = mutableListOf<JsonValue>()

    //2º Objeto
    val nome2 = JsonValue("Nome", JsonString("Neves"))
    val idade2 = JsonValue("Idade", JsonNumber(22F))
    val existe2 = JsonValue("Existe", JsonBoolean(true))

    list2.add(nome2)
    list2.add(idade2)
    list2.add(existe2)

    val neves = JsonObject(list2)

    val nevesObj = JsonValue("Neves", neves)
    listarray.add(nevesObj)

    list.add(nevesObj)

    var listobj = mutableListOf<JsonObject>()
    listobj.add(joao)
    listobj.add(neves)

    val json = JSON(listobj)
    println(json.writeText())

    println(findStrings(json))
    println(findCertain(json, "Idade"))
}
