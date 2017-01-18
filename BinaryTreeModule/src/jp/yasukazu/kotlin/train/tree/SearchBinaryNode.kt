package jp.yasukazu.kotlin.train.tree

/**
 * protected BinarySearchNode
 * Created by Yasukazu on 2017/01/18.
 */

enum class InsertedPos { LEFT, NEW, RIGHT, NONE }

interface SearchBinaryNodeInterface<T: Comparable<T>>{
    var key: T
    var left: SearchBinaryNodeInterface<T>?
    val right: SearchBinaryNodeInterface<T>?
    fun add(item: T, callback: ((InsertedPos)->Unit)?=null)
    operator fun contains(item: T): Boolean
    fun isLeaf(): Boolean
}

class IllegalAssignmentException(msg: String) : Exception(msg)
open class SearchBinaryNode<T: Comparable<T>> (_key: T) : SearchBinaryNodeInterface<T>{
    data class BinaryNodeData<T: Comparable<T>> (var key: T, var left: BinaryNodeData<T>? = null, var right: BinaryNodeData<T>? = null)
    private var data = BinaryNodeData(_key)
    constructor(nodeData: BinaryNodeData<T>) : this(nodeData.key){
        data = nodeData
    }
    override var key: T
        get() {return data.key}
        set(newKey) {
            if (newKey in this)
                throw IllegalAssignmentException("a key already exists in this node tree!")
            with(data) {
                if ((left != null && right != null) && (newKey < left!!.key || newKey > right!!.key ))
                    throw IllegalAssignmentException("new key is smaller than this key or larger than this key!")
                else if (right == null && newKey < left!!.key)
                    throw IllegalAssignmentException("new key is smaller than left key!!")
                else if (left == null && newKey > right!!.key)
                    throw IllegalAssignmentException("new key is larger than right key!")
                key = newKey
            }
        }
    override fun isLeaf() = data.left == null && data.right == null

    override var left: SearchBinaryNodeInterface<T>?
        get() {
          return if(data.left != null) this(data.left!!) else null
        }
        set(newNodeOrNull){
            if (newNodeOrNull != null)
                throw IllegalAssignmentException("Left assignment is only to null!")
            with(data){
                if (left == null)
                    return
               else if (left!!.left != null && left!!.right != null) {
                   throw IllegalAssignmentException("Left has child(ren)!")
               }
               left = null
            }
        }
    override val right: SearchBinaryNode<T>?
        get() {
            return if(data.right != null) this(data.right!!) else null
        }
    /**
     * find key
     */
    enum class FindResult {NOT_FOUND, FOUND}
    fun find(item: T):Boolean{
        class _BreakException(val e: FindResult): Exception()
        /**
         * pre-find
         * return non-null if found
         */
        var result = false
        tailrec fun _find(nodeData: BinaryNodeData<T>?){
            if (nodeData == null) {
                throw _BreakException(FindResult.NOT_FOUND)
            } else { // Otherwise, recur down the tree
                if (item < nodeData.key) {
                    _find(nodeData.left)
                } else if (item > nodeData.key) {
                    _find(nodeData.right)
                } else {
                    throw _BreakException(FindResult.FOUND)
                }
            }
        }
        try {
            _find(data)
        } catch (e: _BreakException){
            result = e.e == FindResult.FOUND
        }
        return result
    }

    /**
     * in operator
     */
    override operator fun contains(item: T) = find(item)
    open class InsertDeleteException: Exception()
    open class InsertException: InsertDeleteException()
    class InsertFailException: InsertException()

    /**
     * add
     * @throws InsertFailException
     */
    override fun add(item: T, callback: ((InsertedPos)->Unit)?){
        class _BreakException(val pos: InsertedPos) : Exception()
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
            _insert(data, item)
        }
        catch (e: _BreakException){
            if (callback != null)
                callback(e.pos)
        }
        return
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

interface SearchBinaryTreeInterface<T: Comparable<T>> {
    val root: SearchBinaryNode<T>?
    fun add(item: T)
    fun remove(item: T)
    fun preTraverse(callback: (T)->Unit)
    fun inTraverse(callback: (T)->Unit)
    fun postTraverse(callback: (T)->Unit)
}

class SearchBinaryTree<T: Comparable<T>>(node: SearchBinaryNode<T>?=null) : SearchBinaryTreeInterface<T> {
    var rootNode = node
    override val root: SearchBinaryNode<T>?
            get() { return rootNode }
    override fun remove(item: T) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun add(item: T){
        if (rootNode != null)
            rootNode!!.add(item)
        else
            rootNode = SearchBinaryNode(item)
    }

    override fun preTraverse(callback: (T) -> Unit){
        fun _traverse(node: SearchBinaryNodeInterface<T>?){
            if (node == null)
                return
            else {
                callback(node.key)
                _traverse(node.left)
                _traverse(node.right)
            }
        }
        _traverse(rootNode)
    }
    override fun inTraverse(callback: (T) -> Unit){
        fun _inTraverse(node: SearchBinaryNodeInterface<T>?){
            if (node == null)
                return
            else {
                _inTraverse(node.left)
                callback(node.key)
                _inTraverse(node.right)
            }
        }
        _inTraverse(rootNode)
    }
    override fun postTraverse(callback: (T) -> Unit){
        fun _traverse(node: SearchBinaryNodeInterface<T>?){
            if (node == null)
                return
            else {
                _traverse(node.left)
                _traverse(node.right)
                callback(node.key)
            }
        }
        _traverse(rootNode)
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
    val item1 = 5
    val node1 = SearchBinaryNode(item1)
    println("As parameter=$item1, SearchBinaryNode is generated: $node1")
    println(if (item1 in node1) "node1 contains $item1." else "node1 does not contains $item1.")
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
    println("Node 1(count=${node1.count}): $node1")
    println("Node 2(count=${node2.count}): $node2")
    val tree = SearchBinaryTree<Int>()
    tree.add(7)
    tree.add(9)
    tree.add(5)
    println("Pre-order Traversal:")
    tree.preTraverse(::println)
    println()
    println("In-order Traversal:")
    tree.inTraverse(::println)
    println()
    println("Post-order Traversal:")
    tree.postTraverse(::println)
    println("Make a copy of tree 1:")
    val tree_root_copy = tree.root?.copy()
    val tree2 = SearchBinaryTree(tree_root_copy)
    tree2.inTraverse ( ::println )
    println()
    tree2.add(1)
    tree2.inTraverse (::println)
}

