import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.functions

interface Type

enum class ValueType : Type {
    INT,
    INT_ARRAY,
    LONG,
    LONG_ARRAY,
    BOOLEAN,
    BOOLEAN_ARRAY,
    STRING,
    TREE_NODE,
}

class ArrayType(val innerType: Type): Type

fun parseParameter(type: KType): Type {
    val classifier = type.classifier as KClass<*>

    return when (classifier.qualifiedName) {
        Int::class.qualifiedName -> ValueType.INT
        IntArray::class.qualifiedName -> ValueType.INT_ARRAY
        Long::class.qualifiedName -> ValueType.LONG
        LongArray::class.qualifiedName -> ValueType.LONG_ARRAY
        Boolean::class.qualifiedName -> ValueType.BOOLEAN
        BooleanArray::class.qualifiedName -> ValueType.BOOLEAN_ARRAY
        String::class.qualifiedName -> ValueType.STRING
        TreeNode::class.qualifiedName -> ValueType.TREE_NODE
        Array::class.qualifiedName -> {
            val arguments = type.arguments
            assert(arguments.size == 1)
            val innerKType = arguments[0].type
            val innerType = parseParameter(innerKType!!)
            return ArrayType(innerType)
        }
        else -> throw IllegalArgumentException("Unknown type: ${classifier.qualifiedName}")
    }
}


class InputCodecs {
    private fun decodeInt(element: JsonElement): Int {
        return Json.decodeFromString(element.toString())
    }

    private fun decodeIntArray(element: JsonElement): IntArray {
        return Json.decodeFromString(element.toString())
    }

    private fun decodeBoolean(element: JsonElement): Boolean {
        return Json.decodeFromString(element.toString())
    }

    private fun decodeBooleanArray(element: JsonElement): BooleanArray {
        return Json.decodeFromString(element.toString())
    }

    private fun decodeLong(element: JsonElement): Long {
        return Json.decodeFromString(element.toString())
    }

    private fun decodeLongArray(element: JsonElement): LongArray {
        return Json.decodeFromString(element.toString())
    }

    private fun decodeString(element: JsonElement): String {
        return Json.decodeFromString(element.toString())
    }

    private fun decodeTreeNodeType(element: JsonElement): TreeNode? {
        val nodes = Json.decodeFromString<Array<Int?>>(element.toString())
        println(nodes)
        val rootVal = nodes[0]
        if (nodes.isEmpty() || rootVal == null) {
            return null
        }
        val root = TreeNode(rootVal)
        var nodesList = mutableListOf<TreeNode?>(root)
        var index = 1
        var nextLevelSize = 2
        while (index < nodes.size) {
            val nextLevelNodes = MutableList<TreeNode?>(nextLevelSize) {null}
            for (curIndex in 0 until nextLevelSize) {
                val parent = nodesList[curIndex/2]!!
                val newVal = nodes.getOrNull(index + curIndex)
                if (newVal == null) {
                    nextLevelNodes[curIndex] = null
                    continue
                }
                val newNode = TreeNode(newVal)
                if (curIndex.rem(2) == 0) {
                    parent.left = newNode
                } else {
                    parent.right = newNode
                }
                nextLevelNodes[curIndex] = newNode
            }
            index += nextLevelSize
            nextLevelSize *= 2
            nodesList = nextLevelNodes
        }

        return root
    }

    fun convertTreeNodeToArray(root: TreeNode?): List<Int?> {
        if (root == null) {
            return listOf()
        }
        val res = mutableListOf<Int?>(root.`val`)
        var curLevel = listOf<TreeNode?>(root)
        while (true) {
            val nextLevel = mutableListOf<TreeNode?>()
            for (cur in curLevel) {
                nextLevel.add(cur?.left)
                nextLevel.add(cur?.right)
            }
            if (nextLevel.all { it == null }) {
                break
            }
            nextLevel.forEach { res.add(it?.`val`) }
            curLevel = nextLevel
        }
        return res
    }

    @Suppress("UNCHECKED_CAST")
    private fun decode(element: JsonElement, type: Type): Any? {
        when (type) {
            is ValueType -> {
                return when (type) {
                    ValueType.INT -> decodeInt(element)
                    ValueType.INT_ARRAY -> decodeIntArray(element)
                    ValueType.BOOLEAN -> decodeBoolean(element)
                    ValueType.BOOLEAN_ARRAY -> decodeBooleanArray(element)
                    ValueType.LONG -> decodeLong(element)
                    ValueType.LONG_ARRAY -> decodeLongArray(element)
                    ValueType.STRING -> decodeString(element)
                    ValueType.TREE_NODE -> decodeTreeNodeType(element)
                }
            }
            is ArrayType -> {
                if (element !is JsonArray) {
                    throw IllegalArgumentException("Element is not JSON Array: $element")
                }
                val innerType = type.innerType
                val result = element.map { decode(it, innerType) }
                return when (innerType) {
                    ValueType.INT -> (result as List<Int>).toTypedArray()
                    ValueType.INT_ARRAY -> (result as List<IntArray>).toTypedArray()
                    ValueType.BOOLEAN -> (result as List<Boolean>).toTypedArray()
                    ValueType.BOOLEAN_ARRAY -> (result as List<BooleanArray>).toTypedArray()
                    ValueType.LONG -> (result as List<Long>).toTypedArray()
                    ValueType.LONG_ARRAY -> (result as List<LongArray>).toTypedArray()
                    ValueType.STRING -> (result as List<String>).toTypedArray()
                    else -> throw IllegalArgumentException("Element is not JSON Array: $element")
                }
            }
            else -> throw IllegalArgumentException("Unsupported type: $type")
        }
    }

    fun decodeRawInput(input: String, type: Type): Any? {
        val jsonElement = Json.parseToJsonElement(input)
        return decode(jsonElement, type)
    }
}

fun parseInputToValues(kFunction: KFunction<*>): List<Type> {
    return kFunction.parameters.asSequence().drop(1).map {
        assert(it.kind == KParameter.Kind.VALUE)
        parseParameter(it.type)
    }.toList()
}

fun extractValuesFromInputs(inputs: List<String>, types: List<Type>): Array<Any?> {
    assert(inputs.size == types.size) {
        "Input sizes ${inputs.size} is different from type sizes ${types.size}"
    }
    val codecs = InputCodecs()
    return inputs.zip(types).map {
        codecs.decodeRawInput(it.first, it.second)
    }.toTypedArray()
}

fun extractFunction(kClass: KClass<*>, methodName: String? = null): KFunction<*> {
    return kClass.functions.first {
        methodName == null || methodName == it.name
    }
}

fun call(solution: Solution, inputs: List<String>, methodName: String? = null): Any? {
    val kFunction = extractFunction(solution::class)
    val types = parseInputToValues(kFunction)
    val arguments =extractValuesFromInputs(inputs, types)
    return kFunction.call(solution, *arguments)
}

fun main() {
    val inputs = File("src/main/kotlin/inputs.txt").readLines()

    val solution = Solution()
    val result = call(solution, inputs)

    if (result is TreeNode) {
        val results = InputCodecs().convertTreeNodeToArray(result)
        println(results)
    } else {
        println(result)
    }
}
