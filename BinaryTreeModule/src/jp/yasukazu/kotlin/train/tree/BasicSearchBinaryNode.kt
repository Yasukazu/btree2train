package jp.yasukazu.kotlin.train.tree

open class BasicSearchBinaryNode<T: Comparable<T>> (private var _key: T) : SearchBinaryNodeInterface<T> {
    //private data class BinaryNodeData<T : Comparable<T>>(var key: T, var left: BinaryNodeData<T>? = null, var right: BinaryNodeData<T>? = null)

    //private var data = BinaryNodeData(_key)

    //private constructor(nodeData: BinaryNodeData<T>) : this(nodeData.key) { data = nodeData }

    override var key: T
        get() {
            return _key
        }
        set(newKey) {
            if (newKey in this)
                throw IllegalAssignmentException("a key already exists in this node tree!")
                if ((left != null && right != null) && (newKey < left!!.key || newKey > right!!.key))
                    throw IllegalAssignmentException("new key is smaller than this key or larger than this key!")
                else if (right == null && newKey < left!!.key)
                    throw IllegalAssignmentException("new key is smaller than left key!!")
                else if (left == null && newKey > right!!.key)
                    throw IllegalAssignmentException("new key is larger than right key!")
                key = newKey
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

    private var _left: BasicSearchBinaryNode<T>? = null
    override var left: SearchBinaryNodeInterface<T>?
        get() = _left
        set(newNodeOrNull) {
            /* if (_left != null){
                    with(_left) {
                        if (_left != null || _right != null)
                            throw IllegalAssignmentException("Left has child(ren)!")
                    }
                }*/
                if (newNodeOrNull != null)
                    _left = newNodeOrNull as BasicSearchBinaryNode<T>
                else
                    _left = null
        }
    private var _right: BasicSearchBinaryNode<T>? = null
    override var right: SearchBinaryNodeInterface<T>?
        get() = _right
        set(newNodeOrNull) {
            /* if (_right != null) {
                    with(_right) {
                        if (_left != null || _right != null)
                            throw IllegalAssignmentException("Right has child(ren)!")
                    }
                }*/
                if (newNodeOrNull != null)
                    _right = newNodeOrNull as BasicSearchBinaryNode<T>
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

