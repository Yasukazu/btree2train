package jp.yasukazu.kotlin.train.tree

open class BasicSearchBinaryNode<T: Comparable<T>> (item: T) : SearchBinaryNodeInterface<T> {
    //private data class BinaryNodeData<T : Comparable<T>>(var key: T, var left: BinaryNodeData<T>? = null, var right: BinaryNodeData<T>? = null)

    //private var data = BinaryNodeData(_key)

    //private constructor(nodeData: BinaryNodeData<T>) : this(nodeData.key) { data = nodeData }

    private var _key = item
    override var key: T get() = _key
        set(newKey) {
            if (newKey in this)
                throw IllegalAssignmentException("a key already exists in this node tree!")
            if (left != null && newKey < left!!.key)
                throw IllegalAssignmentException("new key is smaller than left key!!")
            else if (right != null && newKey > right!!.key)
                throw IllegalAssignmentException("new key is larger than right key!")
            _key = newKey
        }

    private var _left: BasicSearchBinaryNode<T>? = null
    override var left: SearchBinaryNodeInterface<T>?
        get() = _left
        set(newNodeOrNull) {
            if (newNodeOrNull != null) {
                if (newNodeOrNull.key < key)
                    _left = newNodeOrNull as BasicSearchBinaryNode<T>
                else
                    throw IllegalAssignmentException("Left key must be less than key!")
            }
            else
                _left = null
        }

    private var _right: BasicSearchBinaryNode<T>? = null
    override var right: SearchBinaryNodeInterface<T>?
        get() = _right
        set(newNodeOrNull) {
            if (newNodeOrNull != null) {
                if (newNodeOrNull.key > key)
                    _right = newNodeOrNull as BasicSearchBinaryNode<T>
                else
                    throw IllegalAssignmentException("Left key must be greater than key!")
            }
            else
                _right = null
        }

    override fun new(key: T) = this(key)

    override fun toString():String{
        return "BasicSearchBinaryNode: $key(${left?.key}, ${right?.key})"
    }

    private operator fun invoke(item: T): BasicSearchBinaryNode<T>{
        val newNode = BasicSearchBinaryNode(item)
        return newNode
    }

    operator fun get(i: Int): BasicSearchBinaryNode<T>? {
        return when(i){
            0 -> if(left == null) null else left as BasicSearchBinaryNode<T>
            else -> if(right == null) null else right as BasicSearchBinaryNode<T>
        }
    }

    val size: Int get(){ return (if (this[0] != null) 1 else 0) + (if (this[1] != null) 1 else 0) }



    override fun copy(): SearchBinaryNodeInterface<T>{
        val newNode = this(key)
        this.traverse { if (it < key || it > key)
            newNode.add(it) }
        return newNode
    }

    /*
    override tailrec fun _find(item: T, node: SearchBinaryNodeInterface<T>?, parent: SearchBinaryNodeInterface<T>?, callback: ((SearchBinaryNodeInterface<T>?, SearchBinaryNodeInterface<T>?)->Unit)?){
        if (node == null) {
            if (callback != null)
                callback(node, parent)
            throw _FoundException(false)
        } else { // Otherwise, recur down the tree
            if (item < (node as BasicSearchBinaryNode).data.key) {
                _find(item, node.left, node, callback)
            } else if (item > node.data.key) {
                _find(item, node.right, node, callback)
            } else {
                if (callback != null)
                    callback(node, parent)
                throw _FoundException(true)
            }
        }
    }*/
}

