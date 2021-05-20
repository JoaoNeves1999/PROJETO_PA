import org.eclipse.swt.SWT
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.events.ModifyListener
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.ImageData
import org.eclipse.swt.layout.*
import org.eclipse.swt.widgets.*
import java.awt.Dimension
import java.awt.Point
import java.io.File
import javax.swing.JButton

class JSONTree() {
    val shell: Shell
    val tree: Tree

    @Inject
    private lateinit var setup: JSONPlugin

    @InjectAdd
    private var actions = mutableListOf<Action>()//(Validate(), NewFile(), OpenWindow())

    init {
        shell = Shell(Display.getDefault())
        shell.setSize(1500, 750)
        shell.text = "JSON Window"
        shell.layout = GridLayout(2,true)

        tree = Tree(shell, SWT.SINGLE or SWT.BORDER)
        val gdtree = GridData()
        gdtree.widthHint = 300
        gdtree.heightHint = 700
        tree.layoutData = gdtree

        val label = Label(shell, SWT.NONE)
        val gdlabel = GridData()
        gdlabel.widthHint = 250
        gdlabel.heightHint = 500
        label.layoutData = gdlabel

        tree.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                println("selected: " + tree.selection.first().data)
                val item = tree.selection.first()
                label.text = getData(item)

            }
        })

        val search = Text(shell, SWT.SINGLE)
        val gdsearch = GridData()
        gdsearch.widthHint = 100
        search.layoutData = gdsearch

        search.addModifyListener(object: ModifyListener {
            override fun modifyText(e: ModifyEvent) {
                var items = tree.items
                var content = search.text
                var allItems = mutableListOf<TreeItem>()
                items.forEach{
                    var allItems = getAllItems(it, allItems)
                    allItems.forEach {
                        if(!(content == "")){
                            if (search(it, content)) {
                                it.setBackground(Color(0, 255, 0))
                            }else{
                                it.setBackground(Color(255,255,255))
                            }
                        }else{
                            it.setBackground(Color(255,255,255))
                        }
                    }
                }
            }
        })

    }

    //Devolver os dados de um dado item
    fun getData(item: TreeItem): String{
        val text = item.data
        return "$text"
    }

    //Verificar se um dado item possui o mesmo texto atribuído na search
    fun search(item: TreeItem, find: String): Boolean {
        var bool = false
        if (item.text.toString().contains(find)) {
            bool = true
        }
        return bool
    }

    //Obter lista de todos os TreeItem
    fun getAllItems(currentItem: TreeItem, allItems: MutableList<TreeItem>) : MutableList<TreeItem>{
        var allItems = allItems
        allItems.add(currentItem)
        for(i in currentItem.items.indices){
            allItems.add(currentItem.getItem(i))
            for(j in currentItem.items.indices){
                allItems = getAllItems(currentItem.getItem(j), allItems)
            }
        }
        return allItems
    }

    fun open(root: JSON) {
        var parent: TreeItem? = null
        var beginning = true

        shell.text = setup.title

        root.accept(object : Visitor {
            override fun visit(obj: JsonObject) {
                if (beginning == true) {
                    val item = TreeItem(tree, SWT.NONE)
                    handleItem(item, obj)
                    parent = item
                    beginning = false
                } else {
                    val item = TreeItem(parent, SWT.NONE)
                    handleItem(item, obj)
                    parent = item
                }
            }

            override fun endVisit(obj: JsonObject) {
                if (parent?.parentItem != null) {
                    parent = parent!!.parentItem
                } else if (parent?.parentItem == null) {
                    beginning = true
                }
            }

            override fun visit(value: JsonValue) {
                if (!(value.key is JsonObject)) {
                    if (!(value.key is JsonArray)) {
                        if (parent?.text == "[Array]") {
                            val item = TreeItem(parent, SWT.NONE)
                            item.text = setup.changeTextSub(value)
                            item.data = value.key?.writeText()
                            item.setImage(setup.changeImage(value, shell))
                            setup.excludeItem(item, value)
                        } else {
                            val item = TreeItem(parent, SWT.NONE)
                            handleItem(item, value)
                        }
                    }
                }
            }

            override fun visit(array: JsonArray) {
                var item = TreeItem(parent, SWT.NONE)
                parent = item
                handleItem(item, array)
            }

            override fun endVisit(array: JsonArray) {
                parent = parent!!.parentItem
            }
        })
        tree.expandAll()
        actions.forEach { action ->
            val button = Button(shell, SWT.PUSH)
            button.text = action.name
            button.addSelectionListener(object: SelectionAdapter(){
                override fun widgetSelected(e: SelectionEvent){
                    action.execute(this@JSONTree)
                }
            })
        }
        shell.pack()
        shell.open()
        val display = Display.getDefault()
        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) display.sleep()
        }
        display.dispose()
    }

    fun handleItem(item: TreeItem, element: Element){
        if(element is JsonObject){
            item.text = setup.changeText(element)
            item.data = element.writeText()
            item.setImage(setup.changeImage(element, shell))
            setup.excludeItem(item, element)
        }else if (element is JsonValue){
            item.text = setup.changeText(element)
            item.data = element.writeText()
            item.setImage(setup.changeImage(element, shell))
            setup.excludeItem(item, element)
        }else if (element is JsonArray){
            item.text = setup.changeText(element)
            item.data = element.writeText()
            item.setImage(setup.changeImage(element, shell))
            setup.excludeItem(item, element)
        }
    }

    fun validate(validate: String) {
        var items = tree.items
        var allItems = mutableListOf<TreeItem>()
        val label = Label(shell, SWT.NONE)
        items.forEach {
            var allItems = getAllItems(it, allItems)
            allItems.forEach {
                if (search(it, validate) == true) {
                    label.text = "VALID"
                }
            }
        }
    }

    fun createFile(){
        val item = tree.selection.first()
        val filename = "SELECTED_DATA"
        var file = File(filename)
        file.writeText(item.data.toString())
        println("GENERATING...")
    }

    fun openWindow(){
        val item = tree.selection.first()
        val newShell = Shell(Display.getDefault())
        newShell.setSize(250, 500)
        newShell.text = "JSON Data Window"
        newShell.layout = GridLayout(2,true)
        val newLabel = Label(newShell, SWT.NONE)
        newLabel.text = item.data.toString()
        newShell.pack()
        newShell.open()
        val display = Display.getDefault()
        while (!newShell.isDisposed) {
            if (!display.readAndDispatch()) display.sleep()
        }
        newShell.dispose()

    }
}

// auxiliares para varrer a árvore

fun Tree.expandAll() = traverse { it.expanded = true }

fun Tree.traverse(visitor: (TreeItem) -> Unit) {
    fun TreeItem.traverse() {
        visitor(this)
        items.forEach {
            it.traverse()
        }
    }
    items.forEach { it.traverse() }
}

fun main() {

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

    JSONTree().open(json)
}
