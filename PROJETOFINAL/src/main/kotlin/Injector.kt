import java.io.File
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.isAccessible

@Target(AnnotationTarget.PROPERTY)
annotation class Inject

@Target(AnnotationTarget.PROPERTY)
annotation class InjectAdd

class Injector {
    companion object {
        val map: MutableMap<String, MutableList<KClass<*>>> =
            mutableMapOf()
        val classList = mutableListOf<KClass<*>>()

        init {
            val scanner = Scanner(File("di.properties"))
            while (scanner.hasNextLine()) {
                val line = scanner.nextLine()
                val parts = line.split("=")
                if (parts[1] != "") {
                    parts[1].split(",").forEach {
                        classList.add(Class.forName(it).kotlin)
                    }
                    map[parts[0]] = classList
                }
            }
            scanner.close()
        }

        fun <T:Any> create(type: KClass<T>) : T {
            val o: T = type.createInstance()
            type.declaredMemberProperties.forEach {
                if(it.hasAnnotation<Inject>()) {
                    it.isAccessible = true
                    val key = type.simpleName + "." + it.name
                    val obj = map[key]!![0].createInstance()
                    (it as KMutableProperty<*>).setter.call(o, obj)
                }else if(it.hasAnnotation<InjectAdd>()){
                    var prevIt = it
                    prevIt.isAccessible = true
                    val key = type.simpleName + "." + it.name
                    map[key]!!.forEach{
                        if(map[key] != null) {
                            if (it != map[key]!![0]) {
                                val obj = it.createInstance()
                                val actionList = prevIt.getter.call(o) as MutableList<Any>
                                actionList.add(obj)
                            }
                        }
                    }
                }
            }
            return o
        }
    }

}