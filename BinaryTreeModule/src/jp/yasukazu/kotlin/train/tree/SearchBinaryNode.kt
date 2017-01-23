package jp.yasukazu.kotlin.train.tree

class IllegalAssignmentException(msg: String) : Exception(msg)
open class SearchBinaryNode<T: Comparable<T>> (_key: T) : SearchBinaryNodeInterface<T> {
    private data class BinaryNodeData<T : Comparable<T>>(var key: T, var left: BinaryNodeData<T>? = null, var right: BinaryNodeData<T>? = null)

    private var data = BinaryNodeData(_key)

    private constructor(nodeData: BinaryNodeData<T>) : this(nodeData.key) {
        data = nodeData
    }

    override var key: T
        get() {
            return data.key
        }
        set(newKey) {
            if (newKey in this)
                throw IllegalAssignmentException("a key already exists in this node tree!")
            with(data) {
                if ((left != null && right != null) && (newKey < left!!.key || newKey > right!!.key))
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

    override val min: T get() {
        return _getMinNode(this).key
    }

    /**
     * find maximum key node
     */
    tailrec fun _getMaxNode(node: SearchBinaryNodeInterface<T>): SearchBinaryNodeInterface<T> {
        if (node.right == null)
            return node
        else
            return _getMaxNode(node.right!!)
    }

    override val max: T get() {
        return _getMaxNode(this).key
    }

    override var left: SearchBinaryNodeInterface<T>?
        get() {
            return if (data.left != null) this(data.left!!) else null
        }
        set(newNodeOrNull) {
            //if (newNodeOrNull != null) throw IllegalAssignmentException("Left assignment is only to null!")
            with(data) {
                if (left != null)
                    with(left){
                if (left != null || right != null)
                    throw IllegalAssignmentException("Left has child(ren)!")
                }
                if (newNodeOrNull != null) {
                    left = BinaryNodeData(newNodeOrNull.key)
                }
                else
                    left = null
            }
        }
    override var right: SearchBinaryNodeInterface<T>?
        get() {
            return if (data.right != null) this(data.right!!) else null
        }
        set(newNodeOrNull) {
            //if (newNodeOrNull != null) throw IllegalAssignmentException("right assignment is only to null!")
            with(data) {
                if (right != null)
                    with(right){
                        if (left != null || right != null)
                    throw IllegalAssignmentException("right has child(ren)!")
                }
                if (newNodeOrNull != null)
                    right = BinaryNodeData(newNodeOrNull.key)
                else
                right = null
            }
        }

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

    override fun new(key: T) = this(BinaryNodeData(key))

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
    //val childrenStatus = (if (data.left != null) 1 else 0) + (if (data.right != null) 2 else 0)



    fun copy(): SearchBinaryNode<T>{
         val copy = data.copy()
        return this(copy)
    }


}

