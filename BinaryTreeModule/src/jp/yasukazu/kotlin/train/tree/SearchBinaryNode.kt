package jp.yasukazu.kotlin.train.tree

/**
 * protected BinarySearchNode
 * Created by Yasukazu on 2017/01/18.
 */

open class SearchBinaryNode<T: Comparable<T>> (_key: T) {
    data class BinaryNodeData<T: Comparable<T>> (var key: T, var left: BinaryNodeData<T>? = null, var right: BinaryNodeData<T>? = null)
    private var data = BinaryNodeData(_key)
    constructor(nodeData: BinaryNodeData<T>) : this(nodeData.key){
        data = nodeData
    }
    val key = data.key //: T get() { return data.key }
    val left: SearchBinaryNode<T>?
        get() {
          return if(data.left != null) this(data.left!!) else null
        }
    val right: SearchBinaryNode<T>?
        get() {
            return if(data.right != null) this(data.right!!) else null
        }

    open class InsertDeleteException: Exception()
    open class InsertException: InsertDeleteException()
    class InsertFailException: InsertException()
    enum class InsertedPos { LEFT, NEW, RIGHT, NONE }

    /**
     * add
     * @throws InsertFailException
     */
    fun add(newKey: T): InsertedPos {
        class _BreakException(val pos: InsertedPos) : Exception()
        var insertedPos = InsertedPos.NONE
        tailrec fun _insert(data: BinaryNodeData<T>, newKey: T) {
            if (data.key < newKey || data.key > newKey) {
                val childrenStatus = (if (data.left != null) 1 else 0) + (if (data.right != null) 2 else 0)
                when (childrenStatus) {
                    0 -> {
                        if (newKey < data.key) {
                            data.left = BinaryNodeData(newKey)
                            throw _BreakException(InsertedPos.LEFT)
                        } else {
                            data.right = BinaryNodeData(newKey)
                            throw _BreakException(InsertedPos.RIGHT)
                        }
                    }
                    1 -> {
                        if (newKey > data.key) {
                            data.right = BinaryNodeData(newKey)
                            throw _BreakException(InsertedPos.RIGHT)
                        } else
                            _insert(data.left!!, newKey)
                    }
                    2 -> {
                        if (newKey < data.key) {
                            data.left = BinaryNodeData(newKey)
                            throw  _BreakException(InsertedPos.LEFT)
                        } else
                            _insert(data.right!!, newKey)
                    }
                    3 -> {
                        if (newKey < data.key)
                            _insert(data.left!!, newKey)
                        else
                            _insert(data.right!!, newKey)
                    }
                }
            }
            else
                throw InsertFailException()
        }
        try {
            _insert(data, newKey)
        }
        catch (e: _BreakException){
           insertedPos = e.pos 
        }
        return insertedPos
    }

    override fun toString():String{
        return "SearchBinaryNode: "+ data.toString()
    }

    operator fun invoke(data: BinaryNodeData<T>): SearchBinaryNode<T>{
       val copy = data.copy()
        val newNode = SearchBinaryNode(copy)
        return newNode
    }

    operator fun get(i: Int): SearchBinaryNode<T>? {
        return when(i){
            0 -> if(data.left != null) this(data.left!!) else null
            else -> if(data.right != null) this(data.right!!) else null
        }
    }

    val size: Int
        get(){
            return (if (this[0] != null) 1 else 0) + (if (this[1] != null) 1 else 0)
        }

    val childrenStatus = (if (data.left != null) 1 else 0) + (if (data.right != null) 2 else 0)

    /**
     * recursive count self and iterCount
     */
    val count: Int
        get(){
            var n = 0
            fun _count(node: BinaryNodeData<T>?){
                if (node == null)
                    return
                ++n
                if (node.left != null)
                    _count(node.left)
                if (node.right != null)
                    _count(node.right)
            }
            _count(this.data)
            return n
        }

    fun copy(): SearchBinaryNode<T>{
         val copy = data.copy()
        return this(copy)
    }
}

fun main(args: Array<String>){
    /*
    val nodeData1 = BinaryNodeData(3)
    val nodeData2 = BinaryNodeData(1)
    nodeData1.left = nodeData2
    val nodeData3 = nodeData1.copy()
    println(
            """$nodeData1:hash=${"%X".format(nodeData1.hashCode())},
            |$nodeData2,
            |$nodeData3""")
            */
    val node1 = SearchBinaryNode(5)
    println("As key=${node1.key}, SearchBinaryNode is generated: $node1")
    val newKey = 3
    val retval = node1.add(newKey)
    println("Added $newKey: returned $retval")
    println("$node1")
    val newKey2 = 4
    node1.add(newKey2)
    val node2 = node1.copy()
    println("A copy of SearchBinaryNode is generated.")
    val newKey3 = 7
    node2.add(newKey3)
    println("New key $newKey3 is added to Node 2")
    println("Node 1: $node1")
    println("Node 2: $node2")
}

