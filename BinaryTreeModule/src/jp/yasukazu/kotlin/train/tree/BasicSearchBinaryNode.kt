package jp.yasukazu.kotlin.train.tree

open class BasicSearchBinaryNode<T: Comparable<T>> (item: T) : SearchBinaryNode<T> {
    //private data class BinaryNodeData<T : Comparable<T>>(var key: T, var left: BinaryNodeData<T>? = null, var right: BinaryNodeData<T>? = null)

    //private var data = BinaryNodeData(_key)

    //private constructor(nodeData: BinaryNodeData<T>) : this(nodeData.key) { data = nodeData }

    //private val _key = item
    override val key = item//_key//: T get(){return _key}
    /*
        set(newKey) {
            if (newKey in this)
                throw IllegalAssignmentException("a key already exists in this node tree!")
            if (left != null && newKey < left!!.key)
                throw IllegalAssignmentException("new key is smaller than left key!!")
            else if (right != null && newKey > right!!.key)
                throw IllegalAssignmentException("new key is larger than right key!")
            _key = newKey
        }*/

    private var _left: BasicSearchBinaryNode<T>? = null
    override val left: SearchBinaryNode<T>? get(){return _left}
    /*
        set(newNodeOrNull) {
            if (newNodeOrNull != null) {
                if (newNodeOrNull.key < key)
                    _left = newNodeOrNull as BasicSearchBinaryNode<T>
                else
                    throw IllegalAssignmentException("Left key must be less than key!")
            }
            else
                _left = null
        } */

    private var _right: BasicSearchBinaryNode<T>? = null
    override val right: SearchBinaryNode<T>? get(){return _right}
    /*
        set(newNodeOrNull) {
            if (newNodeOrNull != null) {
                if (newNodeOrNull.key > key)
                    _right = newNodeOrNull as BasicSearchBinaryNode<T>
                else
                    throw IllegalAssignmentException("Left key must be greater than key!")
            }
            else
                _right = null
        }*/
    override fun remove(item: T) = _delete_node(item, this, null)
    fun _delete_node(item: T, _self_: BasicSearchBinaryNode<T>?, _parent_: BasicSearchBinaryNode<T>?): DeleteResult {
        var _self: BasicSearchBinaryNode<T>? = _self_
        var _parent: BasicSearchBinaryNode<T>? = _parent_
        // Delete _self binaryNode
        fun __delete_self_node(self: BasicSearchBinaryNode<T>, parent: BasicSearchBinaryNode<T>?): DeleteResult {
            if (parent == null)
                throw DeleteFailException("Self is unremovable!")
            else {
                assert(self.isLeaf()) {"Self must be leaf!"}
                if (parent.left == self)
                    parent._left = null
                else if (parent.right == self)
                    parent._right = null
                else
                    assert(true) { "Unable to delete _self: Parent has no _self!" }
                return DeleteResult.SELF_DELETE
            }
        }

        // replace _self with 1 child
        fun __replace(self: BasicSearchBinaryNode<T>, parent: BasicSearchBinaryNode<T>?): DeleteResult {
            if (parent == null)
                throw DeleteFailException("Unable to remove the root node!")
            else {
                if (self.right == null && self.left != null) {
                    if (parent._left == self)
                        parent._left = self._left
                    else
                        parent._right = self._left
                    return DeleteResult.LEFT_REPLACE
                }
                else if (self.right != null && self.left == null) {
                    if (parent._left == self)
                        parent._left = self._right
                    else
                        parent._right = self._right
                    return DeleteResult.RIGHT_REPLACE
                }
                else {
                    assert(true) { "left or right must be non-null!" }
                    return DeleteResult.NO_MATCH // never comes here
                }
            }
            /*
            with(self){
                if (right == null && left != null) {
                    _key = left!!.key
                    _right = left!!.right as BasicSearchBinaryNode<T>?
                    _left = left!!.left as BasicSearchBinaryNode<T>?
                    return DeleteResult.LEFT_REPLACE
                } else if (left == null && right != null) {
                    with(right!!) {
                        _key = key
                        _left = left as BasicSearchBinaryNode<T>?
                        _right = right as BasicSearchBinaryNode<T>?
                        return DeleteResult.RIGHT_REPLACE
                    }
                }
                else {
                    assert(true) { "left or right must be non-null!" }
                    return DeleteResult.NO_MATCH // never comes here
                }
            }*/
        }

        /**
         * replace 2
        2) * Name the binaryNode with the value to be deleted as 'N binaryNode'.  Without deleting N binaryNode, after choosing its
        in-order successor binaryNode (R binaryNode), copy the value of R to N.
         */
        fun __replace2(self: BasicSearchBinaryNode<T>, parent: BasicSearchBinaryNode<T>?): DeleteResult {
            if (parent == null)
                throw DeleteFailException("Unable to remove self!")
            else
            with(self) {
                assert(left != null && right != null) {"Self must have 2 children!"}
                fun getPredecessorNode(_self: BasicSearchBinaryNode<T>, _parent: BasicSearchBinaryNode<T>): Pair<BasicSearchBinaryNode<T>, BasicSearchBinaryNode<T>> {
                    var s = _self
                    var p = _parent
                    while (s.right != null) {
                        p = s // Reserve the _parent first
                        s = s._right!!
                    }
                    return Pair(s, p)
                }
                val (predNode, predParent) = getPredecessorNode(_left!!, this) // in-order predecessor
                if (predParent.right == predNode) {
                    predParent._right = predNode._left // delete maximum-value binaryNode
                }
                else if (predParent.left == predNode) // No need for this code
                    predParent._left = predNode._left // Never reach here
                val newNode = BasicSearchBinaryNode(predNode.key)
                newNode._left = _left
                newNode._right = _right
                if (parent._left == self)
                    parent._left = newNode
                else
                    parent._right = newNode
                //_key = predNode.key
                return DeleteResult.PREDEC_REPLACE
            }
        }

        // code starts here
        var found = false
        try {
            _find(item, this, null) { n, p ->
                _self = n as BasicSearchBinaryNode<T>?
                _parent = p as BasicSearchBinaryNode<T>?
            }
        } catch (ex: _FoundException) {
            found = ex.found
        }
        if (found) {
            assert(_self != null) { "findNode returened (found null null)!" }
            val childCount = _self!!.childCount()
            fun __fail(self: SearchBinaryNode<T>, parent: SearchBinaryNode<T>?): DeleteResult {
                throw DeleteFailException("Single _self node is undeletable!")
            }

            val selfFuns = if(_parent == null) arrayOf(::__fail, ::__replace, ::__replace2) else
                arrayOf(::__delete_self_node, ::__replace, ::__replace2)
            return selfFuns[childCount](_self!!, _parent)
        }
        else
            return DeleteResult.EMPTY
    }

    override fun add(item: T): InsertedPos {
        var _node: BasicSearchBinaryNode<T>? = null
        var _parent: BasicSearchBinaryNode<T>? = null
        var _found = false
        tailrec fun _find(node: BasicSearchBinaryNode<T>?, parent: BasicSearchBinaryNode<T>?){
            if (node == null) {
                _node = node
                _parent = parent
                throw _FoundException(false)
            } else { // Otherwise, recur down the tree
                if (item < node.key) {
                    _find(node._left, node)
                } else if (item > node.key) {
                    _find(node._right, node)
                } else {
                    _node = node
                    _parent = parent
                    throw _FoundException(true)
                }
            }
        }
        try {
            _find(this, null)
        } catch (ex: _FoundException){
            _found = ex.found
        }
        if (_found)
            return InsertedPos.NONE
        assert(_node == null) {"Node must be null!"}
        assert(_parent != null) {"Parent must be non-null!"}
        val newNode = new(item)
        if (item < _parent!!.key) {
            _parent!!._left = newNode
            return InsertedPos.LEFT
        }
        else {
            _parent!!._right = newNode
            return InsertedPos.RIGHT
        }
    }

    override fun new(key: T) = this(key)

    override fun toString():String{
        return "BasicSearchBinaryNode: $key(${_left?.key}, ${_right?.key})"
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



    override fun copy(): SearchBinaryNode<T>{
        val newNode = this(key)
        this.traverse { if (it < key || it > key)
            newNode.add(it) }
        return newNode
    }

    /*
    override tailrec fun _find(item: T, node: SearchBinaryNode<T>?, parent: SearchBinaryNode<T>?, callback: ((SearchBinaryNode<T>?, SearchBinaryNode<T>?)->Unit)?){
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

