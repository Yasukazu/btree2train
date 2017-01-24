package jp.yasukazu.kotlin.train.tree

/**
 *
 * Created by Yasukazu on 2017/01/22.
 */


interface SearchBinaryNodeInterface<T: Comparable<T>>{
    var key: T
    var left: SearchBinaryNodeInterface<T>?
    var right: SearchBinaryNodeInterface<T>?
    fun new(key: T): SearchBinaryNodeInterface<T> // psudo constructor
    fun remove(item: T, callback : ((DeleteResult) -> Unit)?=null){
        _delete_node(item, this, null, callback)
    }
    tailrec fun _delete_node(item: T, self: SearchBinaryNodeInterface<T>?, parent: SearchBinaryNodeInterface<T>?, callback: ((DeleteResult)->Unit)?=null){
        // Delete self binaryNode
        fun __delete_self_node(self: SearchBinaryNodeInterface<T>, parent: SearchBinaryNodeInterface<T>?){
            if (parent == null)
                throw DeleteFailException()
            assert(self.left == null && self.right == null)
                if (parent.left == self)
                    parent.left = null
                else if (parent.right == self)
                    parent.right = null
                else
                    assert(true) {"Unable to delete self: Parent has no self!"}
        }
        // replace self with 1 child
        fun __replace(self: SearchBinaryNodeInterface<T>, child: SearchBinaryNodeInterface<T>, parent: SearchBinaryNodeInterface<T>?){
            if (parent == null)
                throw DeleteFailException()
                if (parent.left == self)
                    parent.left = child
                else if (parent.right == self)
                    parent.right = child
                else
                    assert(true, {"Unable to replace self with a child: Parent has no self!"})
        }
        /**
         * replace 2
        2) * Name the binaryNode with the value to be deleted as 'N binaryNode'.  Without deleting N binaryNode, after choosing its
        in-order successor binaryNode (R binaryNode), copy the value of R to N.
         */
        fun __replace2(self: SearchBinaryNodeInterface<T>){
            assert(self.left != null && self.right != null)
            fun getPredecessorNode(_self: SearchBinaryNodeInterface<T>, _parent: SearchBinaryNodeInterface<T>) : Pair<SearchBinaryNodeInterface<T>, SearchBinaryNodeInterface<T>> {
                var s = _self
                var p = _parent
                while (s.right != null) {
                    p = s // Reserve the parent first
                    s = s.right!!
                }
                return Pair(s, p)
            }
            val (predNode, predParent) = getPredecessorNode(self.left!!, self) // in-order predecessor
            self.key = predNode.key
            if(predParent.right == predNode)
                predParent.right = predNode.left // delete maximum-value binaryNode
            else if(predParent.left == predNode) // No need for this code
                predParent.left = predNode.left // Never reach here
        }
        // code starts here
        if (self == null)
            throw DeleteFailException()//return DeleteResult.NO_MATCH
        else {
            if (item < self.key) {
                return _delete_node(item, self.left, self)
            } else if (item > self.key) {
                return _delete_node(item, self.right, self)
            } else { // item == self.item
                // 3. delete self if no iterCount
                val bL = if(self.left == null) 0 else 1
                val bR = if(self.right == null) 0 else 2
                when(bL or bR){
                    0 -> {
                        __delete_self_node(self, parent)
                        if (callback != null)
                            callback(DeleteResult.SELF_DELETE)
                    }
                    1 -> {
                        __replace(self, self.left!!, parent)
                        if (callback != null)
                            callback(DeleteResult.LEFT_REPLACE)
                    }
                    2 -> {
                        __replace(self, self.right!!, parent)
                        if (callback != null)
                            callback(DeleteResult.RIGHT_REPLACE)
                    }
                    else -> {
                        __replace2(self)
                        if (callback != null)
                            callback(DeleteResult.PREDEC_REPLACE)
                    }
                }
            }
        }
    }

    fun add(item: T, callback: ((SearchBinaryNodeInterface<T>, InsertedPos, SearchBinaryNodeInterface<T>?)->Unit)?=null){
        var _node: SearchBinaryNodeInterface<T>? = null
        var _parent: SearchBinaryNodeInterface<T>? = null
        var _found = false
        tailrec fun _find(node: SearchBinaryNodeInterface<T>?, parent: SearchBinaryNodeInterface<T>?){
            if (node == null) {
                _node = node
                _parent = parent
                throw _FoundException(false)
            } else { // Otherwise, recur down the tree
                if (item < node.key) {
                    _find(node.left, node)
                } else if (item > node.key) {
                    _find(node.right, node)
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
            throw InsertFailException()
        assert(_node == null) {"Node must be null!"}
        assert(_parent != null) {"Parent must be non-null!"}
        if (item < _parent!!.key) {
           val newNode = new(item)
           _parent!!.left = newNode
           if (callback != null)
               callback(newNode, InsertedPos.LEFT, _parent)
        }
        else {
           val newNode = new(item)
           _parent!!.right = newNode
           if (callback != null)
               callback(newNode, InsertedPos.RIGHT, _parent)
        }
    }

    tailrec fun _find(item: T, node: SearchBinaryNodeInterface<T>?, parent: SearchBinaryNodeInterface<T>?, callback: ((SearchBinaryNodeInterface<T>?, SearchBinaryNodeInterface<T>?)->Unit)?=null){
        if (node == null) {
            if (callback != null)
                callback(node, parent)
            throw _FoundException(false)
        } else { // Otherwise, recur down the tree
            if (item < node.key) {
                _find(item, node.left, node, callback)
            } else if (item > node.key) {
                _find(item, node.right, node, callback)
            } else {
                if (callback != null)
                    callback(node, parent)
                throw _FoundException(true)
            }
        }
    }
    fun findNode(item: T): Triple<Boolean, SearchBinaryNodeInterface<T>?, SearchBinaryNodeInterface<T>?> {
        var node: SearchBinaryNodeInterface<T>? = null
        var parent: SearchBinaryNodeInterface<T>? = null
        var found = false
        try {
            _find(item, this, null) { n, p -> node = n
                parent = p}
        } catch (ex: _FoundException){
            found = ex.found
        }
        return Triple(found, node, parent)
    }
    operator fun contains(item: T): Boolean {
        val (found, node, parent) = findNode(item)
        return found
    }
    fun isLeaf() = left == null && right == null
    fun childCount() = (if (left != null) 1 else 0) + (if(right != null) 1 else 0)
    fun memberCount() = childCount() + 1
    fun childStatus() = (if (left != null) 1 else 0) or (if(right != null) 2 else 0)
    val childrenStatus: Int get() = (if (left != null) 1 else 0) + (if (right != null) 2 else 0)
    val min: T
    val max: T
    val total: Int get() { return count()}

    fun copy(): SearchBinaryNodeInterface<T>
    fun count(): Int {
        var n = 0
        traverse { ++n }
        return n
    }

    fun traverse(callback : ((T) -> Unit)?){
        _traverse(this, callback)
    }

    fun _traverse(node: SearchBinaryNodeInterface<T>?, callback : ((T) -> Unit)?){
        if (node == null)
            return
        if (callback != null)
            callback(node.key)
        _traverse(node.left, callback)
        _traverse(node.right, callback)
    }

}
