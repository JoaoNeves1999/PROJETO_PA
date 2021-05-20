import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem
import java.awt.GridLayout
import java.awt.LayoutManager

interface JSONPlugin{
    val title: String
    val itemToDelete: String
    fun changeText(element: Element): String
    fun changeTextSub(value: JsonValue): String
    fun changeImage(element: Element, shell: Shell): Image?
    fun excludeItem(item: TreeItem, element: Element)
}

interface Action {
    val name: String
    fun execute(json: JSONTree)
}

open class DefaultSetup : JSONPlugin {
    open override val title: String
        get() = "JSON Window"

    open override val itemToDelete: String
        get() = ""

    override fun changeText(element: Element): String {
        var text = ""
        if(element is JsonObject){
            text = "(Object)"
        }else if(element is JsonArray){
            text = "[Array]"
        }else if(element is JsonValue){
            text = element.writeText()
        }
        return text
    }

    override fun changeTextSub(value: JsonValue): String {
        return value.key!!.writeText()
    }

    override fun changeImage(element: Element, shell: Shell): Image? {
        return null
    }

    override fun excludeItem(item: TreeItem, element: Element){}
}

class PresentationSetup : JSONPlugin {
    open override val title: String
        get() = "JSON Plugin"

    open override val itemToDelete: String
        get() = "\"Neves\""

    override fun changeText(element: Element): String {
        var text = ""
        if(element is JsonObject){
            text = "Object: " + element.firstVal().key!!.writeText()
        }else if(element is JsonArray){
            text = "Array: " + element.firstVal().key!!.writeText()
        }else if(element is JsonValue){
            text = element.key!!.writeText()
        }
        return text
    }

    override fun changeTextSub(value: JsonValue): String{
        return value.key!!.writeText()
    }

    override fun changeImage(element: Element, shell: Shell): Image? {
        var image: Image
        if(element is JsonObject || element is JsonArray){
            image = Image(shell.display, "C:\\Users\\jotan\\IdeaProjects\\PROJETO\\OBJECT.png")
        }else{
            image = Image(shell.display, "C:\\Users\\jotan\\IdeaProjects\\PROJETO\\VALUE.png")
        }
        return image
    }

    override fun excludeItem(item: TreeItem, element: Element){
        if(element is JsonValue && item.text == itemToDelete ){
            item.dispose()
        }
    }
}

class Validate: Action{
    override val name: String
        get() = "Validate"

    override fun execute(json: JSONTree){
        json.validate("Nome")
    }
}

class NewFile: Action{
    override val name: String
        get() = "New File"

    override fun execute(json: JSONTree){
        json.createFile();
    }
}

class OpenWindow: Action{
    override val name: String
        get() = "Open"

    override fun execute(json: JSONTree) {
        json.openWindow()
    }
}

fun main(){
    val w = Injector.create(JSONTree::class)

    var list = mutableListOf<JsonValue>()
    var listarray = mutableListOf<JsonValue>()
    var listarray2 = mutableListOf<JsonValue>()

    //1º Objeto
    val nome = JsonValue("Nome", JsonString("João"))
    val idade = JsonValue("Idade", JsonNumber(22F))
    val existe = JsonValue("Existe", JsonBoolean(true))
    val array = JsonValue("Array", JsonArray(listarray))
    val segundoArray = JsonValue("Array", JsonArray(listarray2))

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
    val existe2 = JsonValue("Existe", JsonBoolean(false))

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

    w.open(json)
}