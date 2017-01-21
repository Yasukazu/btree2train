package jp.yasukazu.kotlin.train.tree

/**
 * protected BinarySearchNode
 * Created by Yasukazu on 2017/01/18.
 */

enum class InsertedPos { LEFT, NEW, RIGHT, NONE }

/**
 * Node Interface is only about self, right and left
 * includes recursive properties/functions
 */
interface SearchBinaryNodeInterface<T: Comparable<T>>{
    var key: T
    var left: SearchBinaryNodeInterface<T>?
    var right: SearchBinaryNodeInterface<T>?
    fun add(item: T, callback: ((InsertedPos)->Unit)?=null)
    fun findNode(item: T): SearchBinaryNodeInterface<T>?{
        class _FoundException(val found: Boolean, val node: SearchBinaryNodeInterface<T>?): Exception()
        tailrec fun _find(item: T, node: SearchBinaryNodeInterface<T>?){
            if (node == null) {
                throw _FoundException(found=false, node=node)
            } else { // Otherwise, recur down the tree
                if (item < node.key) {
                    _find(item, node.left)
                } else if (item > node.key) {
                    _find(item, node.right)
                } else {
                    throw _FoundException(found=true, node=node)
                }
            }
        }
        try {
            _find(item, this)
        } catch (ex: _FoundException){
            if (ex.found)
                return ex.node
        }
        return null
    }
    operator fun contains(item: T): Boolean // at most 3 members
    fun isLeaf() = left == null && right == null
    fun childCount() = (if (left != null) 1 else 0) + (if(right != null) 1 else 0)
    fun memberCount() = childCount() + 1
    fun childStatus() = (if (left != null) 1 else 0) or (if(right != null) 2 else 0)
    val min: T
    val max: T
    val total: Int get() { return count()}
    /**
     * recursive count self and iterCount
     */
    fun count(): Int {
        var n = 0
        traverse { ++n }
        return n
    }

    fun traverse(callback : ((T) -> Unit)?){
        _traverse(this, callback)
    }

    fun _traverse(node: SearchBinaryNodeInterface<T>?, callback : ((T) -> Unit)?)

}

class IllegalAssignmentException(msg: String) : Exception(msg)
open class SearchBinaryNode<T: Comparable<T>> (_key: T) : SearchBinaryNodeInterface<T>{
    private data class BinaryNodeData<T: Comparable<T>> (var key: T, var left: BinaryNodeData<T>? = null, var right: BinaryNodeData<T>? = null)
    private var data = BinaryNodeData(_key)
    private constructor(nodeData: BinaryNodeData<T>) : this(nodeData.key){
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
    //override fun isLeaf() = data.left == null && data.right == null
    //override val total: Int get() = count()

    /**
     * find minimum key node
     */
    tailrec fun _getMinNode(node: SearchBinaryNodeInterface<T>): SearchBinaryNodeInterface<T> {
        if (node.left == null)
            return node
        else
            return _getMinNode(node.left!!)
    }

    override val min: T get(){ return _getMinNode(this).key }

    /**
     * find maximum key node
     */
    tailrec fun _getMaxNode(node: SearchBinaryNodeInterface<T>): SearchBinaryNodeInterface<T> {
        if (node.right == null)
            return node
        else
            return _getMaxNode(node.right!!)
    }

    override val max: T get(){ return _getMaxNode(this).key }

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
               else if (left!!.left != null || left!!.right != null) {
                   throw IllegalAssignmentException("Left has child(ren)!")
               }
               left = null
            }
        }
    override var right: SearchBinaryNodeInterface<T>?
        get() {
            return if(data.right != null) this(data.right!!) else null
        }
        set(newNodeOrNull){
            if (newNodeOrNull != null)
                throw IllegalAssignmentException("right assignment is only to null!")
            with(data){
                if (right == null)
                    return
                else if (right!!.left != null || right!!.right != null) {
                    throw IllegalAssignmentException("right has child(ren)!")
                }
                right = null
            }
        }
    /**
     * find key
    enum class FindResult {NOT_FOUND, FOUND}
    fun find(item: T): Boolean{
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
     */


    /**
     * in operator
     */
    override operator fun contains(item: T): Boolean {//} = find(item)
        if (item < key || item > key)
            return false
        if (left != null)
                with(left!!) {
                    if (item < key || item > key)
                        return false
                }
        if (right != null)
            with(right!!) {
                if (item < key || item > key)
                    return false
            }
        return true
    }
    open class InsertDeleteException: Exception()
    open class InsertException: InsertDeleteException()
    class InsertFailException: InsertException()

    /**
     * add
     * @throws InsertFailException
     */
    override fun add(item: T, callback: ((InsertedPos)->Unit)?){
        /*
        if (item < key){
            if (left == null)
                left = this(BinaryNodeData(item))
                if (callback != null)
                    callback(InsertedPos.LEFT)
            else
                throw InsertFailException()
        } else if (item > key){
            if (right == null)
                right = this(BinaryNodeData(item))
            if (callback != null)
                callback(InsertedPos.RIGHT)
            else
                throw InsertFailException()
        } else {
            throw InsertFailException()
        }
    }
    */
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

    private operator fun invoke(data: BinaryNodeData<T>): SearchBinaryNode<T>{
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

    val size: Int get(){ return (if (this[0] != null) 1 else 0) + (if (this[1] != null) 1 else 0) }
    val childrenStatus = (if (data.left != null) 1 else 0) + (if (data.right != null) 2 else 0)



    fun copy(): SearchBinaryNode<T>{
         val copy = data.copy()
        return this(copy)
    }




    override  fun _traverse(node: SearchBinaryNodeInterface<T>?, callback : ((T) -> Unit)?){
        if (node == null)
            return
        if (callback != null)
            callback(node.key)
        _traverse(node.left, callback)
        _traverse(node.right, callback)
    }
}


