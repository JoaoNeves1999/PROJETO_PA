import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ProjetoKtTest {

    @Test
    fun generateJson(){
        var list = mutableListOf<JsonValue>()
        var listarray = arrayListOf<JsonValue>()
        var listarray2 = arrayListOf<JsonValue>()

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

        assertEquals("{\n" +
                "\"Nome\": \"João\",\n" +
                "\"Idade\": 22.0,\n" +
                "\"Existe?\": true,\n" +
                "\"Array\": [\"João\", 22.0, true, [\"João\"], {\n" +
                "\"Nome\": \"Neves\",\n" +
                "\"Idade\": 22.0,\n" +
                "\"Existe?\": true\n" +
                "}],\n" +
                "\"Neves\": {\n" +
                "\"Nome\": \"Neves\",\n" +
                "\"Idade\": 22.0,\n" +
                "\"Existe?\": true\n" +
                "}\n" +
                "}\n" +
                "{\n" +
                "\"Nome\": \"Neves\",\n" +
                "\"Idade\": 22.0,\n" +
                "\"Existe?\": true\n" +
                "}\n", json.writeText())
    }

    @Test
    fun findStrings() {
        var list = mutableListOf<JsonValue>()
        var listarray = arrayListOf<JsonValue>()
        var listarray2 = arrayListOf<JsonValue>()

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

        assertEquals("\"João\"\n\"João\"\n\"João\"\n\"Neves\"\n\"Neves\"\n\"Neves\"\n" , findStrings(json))
    }
}