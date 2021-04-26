import kotlin.reflect.KClass

//------------------CLASSES--------------------------------------

abstract class Element(val name: String){
    abstract fun writeText(): String
    abstract fun accept(v: Visitor)
}

class JSON(val listobj: MutableList<JsonObject>): Element("Json"){

    override fun writeText(): String {
        var text: String = ""
        listobj.forEach{
            text += it.writeText() + "\n"
        }
        return text
    }

    override fun accept(v: Visitor){
        v.visit(this)
        listobj.forEach{
            it.accept(v)
        }
    }
}

class JsonValue(val id: String, val key: Element): Element("Value") {

    override fun writeText(): String {
        return "\"$id\"" + ":" + " ${key.writeText()}"
    }

    override fun accept(v: Visitor) {
        v.visit(this)
    }
}

class JsonObject(var listValues: MutableList<JsonValue>): Element("Object") {

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
    }

    fun addValue(jvalue: JsonValue){
        listValues.add(jvalue)
    }

    fun getList(): List<JsonValue>{
        return listValues
    }

}

class JsonArray(var arrayListValues: MutableList<JsonValue>): Element("Array"){

    override fun writeText(): String{
        var text: String = "["
        arrayListValues.forEach{
            if(it == arrayListValues.last()) {
                text += "${it.writeText()}"
            }else{
                text += "${it.writeText()}" + ", "
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
    }

    fun addValue(jvalue: JsonValue){
        arrayListValues.add(jvalue)
    }

    fun getList(): MutableList<JsonValue>{
        return arrayListValues
    }

}

class JsonString(val string: String): Element("String"){

    override fun writeText(): String{
        return "\"$string\""
    }

    override fun accept(v: Visitor) {}
}

class JsonNumber(val float: Float): Element("Number"){

    override fun writeText(): String{
        return "$float"
    }

    override fun accept(v: Visitor) {}
}

class JsonBoolean(val boolean: Boolean): Element("Boolean") {

    override fun writeText(): String{
        return "$boolean"
    }

    override fun accept(v: Visitor) {}
}

interface Visitor{
    fun visit(json: JSON) {}
    fun visit(objects: JsonObject) {}
    fun visit(array: JsonArray) {}
    fun visit(value: JsonValue) {}
}

interface TypeMapping{
    fun mapType(c: KClass<*>): String
    fun mapObject(o: Any?): String
}

class MyJSON : TypeMapping {
    override fun mapType(c: KClass<*>): String {
        var text: String = ""
        when (c) {
            Float::class -> text += "JSONNUMBER"
            Boolean::class -> text += "JSONBOOLEAN"
            String::class -> text += "JSONSTRING"
            MutableList::class -> text += "JSONARRAY"
        }
        return text
    }

    override fun mapObject(o: Any?): String {
        TODO("Not yet implemented")
    } 
}

//------------------FUNCTIONS--------------------------------------

fun findStrings(json: JSON): String{
    val v = object: Visitor {
        var text: String = ""
        override fun visit(obj: JsonObject){
            text += findStrings(obj)
        }
    }
    json.accept(v)
    return v.text
}

fun findCertain(json: JSON, name: String): String{
    val v = object: Visitor{
        var text = ""
        override fun visit(obj: JsonObject){
            text += findCertain(obj, name)
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
    val existe = JsonValue("Existe?", JsonBoolean(true))
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
    val existe2 = JsonValue("Existe?", JsonBoolean(true))

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

//-------------------RESOURCE FUNCTIONS(ALTERAR FUTURAMENTE)------------------
private fun findStrings(obj: JsonObject): String{
    val v = object: Visitor{
        var text: String = ""
        override fun visit(value: JsonValue){
            when(value.key){
                is JsonString -> text += value.key.writeText() + "\n"
                is JsonArray -> text += findStrings(value.key)
                is JsonObject -> text += findStrings(value.key)
            }
        }
    }
    obj.accept(v)
    return v.text
}

private fun findStrings(array: JsonArray): String{
    val v = object: Visitor{
        var text: String = ""
        override fun visit(value: JsonValue){
            when(value.key){
                is JsonString -> text += value.key.writeText() + "\n"
                is JsonArray -> text += findStrings(value.key)
                is JsonObject -> text += findStrings(value.key)
            }
        }
    }
    array.accept(v)
    return v.text
}

private fun findCertain(obj: JsonObject, name: String): String{
    val v = object: Visitor{
        var text: String = ""
        override fun visit(value: JsonValue){
            if(value.id == name){
                text += value.key.writeText() + "\n"
            }else if (value.key is JsonArray){
                text += findCertain(value.key, name)
            }else if (value.key is JsonObject){
                text += findCertain(value.key, name)
            }
        }
    }
    obj.accept(v)
    return v.text
}

private fun findCertain(array: JsonArray, name: String): String{
    val v = object: Visitor{
        var text: String = ""
        override fun visit(value: JsonValue){
            if(value.id == name){
                text += value.key.writeText() + "\n"
            }else if (value.key is JsonArray){
                text += findCertain(value.key, name)
            }else if (value.key is JsonObject){
                text += findCertain(value.key, name)
            }
        }
    }
    array.accept(v)
    return v.text
}