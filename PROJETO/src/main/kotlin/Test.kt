import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.test.assertTrue


@Target(AnnotationTarget.FUNCTION)
annotation class TestCase(val desc: String)

class Test{

    @TestCase("Gerar JSON")
    fun generateJSON(){

    }

    @TestCase("Devolver todas as JSONStrings presentes no JSON")
    fun findStrings(){

    }

    @TestCase("Procurar todos os JSONValues com determinado id")
    fun findCertain(){

    }


}

fun main() {
    val clazz = Test::class
    clazz.declaredMemberFunctions.forEach {
        if (it.hasAnnotation<TestCase>()) {
            val a = it.findAnnotation<TestCase>()
            println(a)
        }
    }
}