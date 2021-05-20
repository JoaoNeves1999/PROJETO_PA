import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation

@Target(AnnotationTarget.PROPERTY)
annotation class Nome

@Target(AnnotationTarget.PROPERTY)
annotation class Idade

@Target(AnnotationTarget.PROPERTY)
annotation class Existe

@Target(AnnotationTarget.PROPERTY)
annotation class Tipo

@Target(AnnotationTarget.PROPERTY)
annotation class Lista

@Target(AnnotationTarget.PROPERTY)
annotation class Mapa

data class Student(
    @Nome
    val Nome: String,
    @Idade
    val Idade: Float,
    @Existe
    val Existe: Boolean,
    @Tipo
    val Tipo: StudentType,
    @Lista
    val Lista: MutableList<*>,
    @Mapa
    val Mapa: MutableMap<String, *>
)

enum class StudentType {
    Bachelor, Master, Doctoral
}

fun mapObject(c: Any?): Element?{
    var element: Element? = null
    if((c!!::class).isData){
        element = JsonObject(dataClassHandling(c))
    }
    when(c){
        is Boolean -> element = JsonBoolean(c)
        is Float -> element = JsonNumber(c)
        is String -> element = JsonString(c)
        is MutableCollection<*> -> element = JsonArray(collectionHandling(c))
        is Map<*,*> -> element = JsonObject(mapHandling(c))
        is Enum<*> -> element = JsonString(enumHandling(c))
    }
    return element
}


//Função auxiliar para tratar das coleções
fun collectionHandling(c: MutableCollection<*>): MutableList<JsonValue>{
    var list = mutableListOf<JsonValue>()
    var value: JsonValue
    c.forEach{
        value = JsonValue("", mapObject(it))
        list.add(value)
    }
    return list
}

//Função auxiliar para tratar dos mapas
fun mapHandling(c: Map<*, *>): MutableList<JsonValue>{
    var list = mutableListOf<JsonValue>()
    var value: JsonValue
    c.forEach{
        value = JsonValue("${it.key}", mapObject(it.value))
        list.add(value)
    }
    return list
}

//Função auxiliar para tratar das data class
fun dataClassHandling(c: Any): MutableList<JsonValue>{
    val clazz: KClass<Any> = c::class as KClass<Any>
    var list = mutableListOf<JsonValue>()
    var value: JsonValue
    clazz.declaredMemberProperties.forEach {
        if(!(it.hasAnnotation<Deprecated>())) {
            value = JsonValue("${it.name}", mapObject(it.call(c)))
            list.add(value)
        }
    }
    return list
}

//Função auxiliar para tratar dos enumerados
fun enumHandling(c: Enum<*>): String{
    return c.name
}

fun main(){
    val s = JsonValue("Nome", mapObject("João"))
    val n = JsonValue("Idade", mapObject(22F))
    val b = JsonValue("Existe?", mapObject(true))
    var teste = mutableListOf<Any>()
    teste.add("João")
    teste.add(22F)
    teste.add(true)

    val value = JsonValue("Lista", mapObject(teste))
    println(value.writeText())

    val map: MutableMap<String, Any> = hashMapOf()
    val map2: MutableMap<String, Any> = hashMapOf()
    var teste2 = mutableListOf<Any>()
    teste2.add("João")
    teste2.add(22F)
    teste2.add(map2)
    teste.add(teste2)

    map["Nome"] = "João"
    map["Idade"] = 22F
    map["Existe"] = true
    map["Lista"] = teste
    map2["Nome"] = "Neves"
    map2["Idade"] = 22F
    map2["Existe"] = true
    map["Map"] = map2
    println(mapObject(map)?.writeText())

    val listStudent = mutableListOf<Any>()
    val mapStudent: MutableMap<String, Any> = hashMapOf()
    mapStudent["Nome"] = "João"
    mapStudent["Idade"] = 22f
    listStudent.add("João")
    listStudent.add(22f)
    val student = Student("João", 22f, true, StudentType.Bachelor, listStudent, mapStudent)
    println(mapObject(student)?.writeText())
}
