package jp.yasukazu.kotlin.train.tree

/**
 *
 * Created by Yasukazu on 2017/01/22.
 */

enum class LeftOrRight {NONE, LEFT, RIGHT}
interface SearchBinaryNode<T: Comparable<T>>{
    var key: T
    //fun setKey(newKey: T)
    var left: SearchBinaryNode<T>? // set() must have restriction
    var right: SearchBinaryNode<T>?
    fun new(key: T): SearchBinaryNode<T> // psudo constructor
    /**
     * remove
     * @return : Pair of delete result and parental node
     */
    fun remove(item: T): Pair<DeleteResult, SearchBinaryNode<T>?> {
        var pair: Pair<DeleteResult, SearchBinaryNode<T>?>? = null
        try {
            pair = _delete_node(item, this, null)
        }
        catch (ex: DeleteFailException){
            return Pair(DeleteResult.NO_MATCH, pair?.second)
        }
        return pair
    }
    tailrec fun _delete_node(item: T, self: SearchBinaryNode<T>?, parent: SearchBinaryNode<T>?): Pair<DeleteResult, SearchBinaryNode<T>?> {
        // Delete self binaryNode
        fun __delete_self_node(self: SearchBinaryNode<T>, parent: SearchBinaryNode<T>?){
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
        fun __replace(self: SearchBinaryNode<T>, child: SearchBinaryNode<T>, parent: SearchBinaryNode<T>?){
            if (parent == null)
                throw DeleteFailException("Parent is null!")
            if (parent.left == self)
                parent.left = child
            else if (parent.right == self)
                parent.right = child
            else
                assert(true) {"Unable to replace self with a child: Parent has no self!"}
        }
        /**
         * replace 2
        2) * Name the binaryNode with the value to be deleted as 'N binaryNode'.  Without deleting N binaryNode, after choosing its
        in-order successor binaryNode (R binaryNode), copy the value of R to N.
         */
        fun __replace2(self: SearchBinaryNode<T>){
            assert(self.left != null && self.right != null)
            fun getPredecessorNode(_self: SearchBinaryNode<T>, _parent: SearchBinaryNode<T>) : Pair<SearchBinaryNode<T>, SearchBinaryNode<T>> {
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
            throw DeleteFailException("Self is null!")//return DeleteResult.NO_MATCH
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
                        return Pair(DeleteResult.SELF_DELETE, parent)
                    }
                    1 -> {
                        __replace(self, self.left!!, parent)
                        return Pair(DeleteResult.LEFT_REPLACE, parent)
                    }
                    2 -> {
                        __replace(self, self.right!!, parent)
                        return Pair(DeleteResult.RIGHT_REPLACE, parent)
                    }
                    else -> {
                        __replace2(self)
                        return Pair(DeleteResult.PREDEC_REPLACE, parent)
                    }
                }
            }
        }
    }

    fun add(item: T): Pair<InsertedPos, SearchBinaryNode<T>?> {
        var _node: SearchBinaryNode<T>? = null
        var _parent: SearchBinaryNode<T>? = null
        var _found = false
        tailrec fun _find(node: SearchBinaryNode<T>?, parent: SearchBinaryNode<T>?){
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
            return Pair(InsertedPos.NONE, _parent)
        assert(_node == null) {"Node must be null!"}
        assert(_parent != null) {"Parent must be non-null!"}
        val newNode = new(item)
        if (item < _parent!!.key) {
           _parent!!.left = newNode
           return Pair(InsertedPos.LEFT, _parent)
        }
        else {
           _parent!!.right = newNode
           return Pair(InsertedPos.RIGHT, _parent)
        }
    }

    tailrec fun _find(item: T, node: SearchBinaryNode<T>?, parent: SearchBinaryNode<T>?, callback: ((SearchBinaryNode<T>?, SearchBinaryNode<T>?)->Unit)?=null){
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
    fun findNode(item: T): Triple<Boolean, SearchBinaryNode<T>?, SearchBinaryNode<T>?> {
        var node: SearchBinaryNode<T>? = null
        var parent: SearchBinaryNode<T>? = null
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

    tailrec fun _getMinNode(node: SearchBinaryNode<T>): SearchBinaryNode<T> {
        if (node.left == null)
            return node
        else
            return _getMinNode(node.left!!)
    }

    val min: T get() {
        return _getMinNode(this).key
    }

    tailrec fun _getMaxNode(node: SearchBinaryNode<T>): SearchBinaryNode<T> {
        if (node.right == null)
            return node
        else
            return _getMaxNode(node.right!!)
    }

    val max: T get() { return _getMaxNode(this).key }
    val total: Int get() { return count()}

    fun copy(): SearchBinaryNode<T>
    fun count(): Int {
        var n = 0
        traverse { ++n }
        return n
    }

    fun traverse(callback : ((T) -> Unit)){
        _traverse(this, callback)
    }

    fun _traverse(node: SearchBinaryNode<T>?, callback : ((T) -> Unit)){
        if (node == null)
            return
        callback(node.key)
        _traverse(node.left, callback)
        _traverse(node.right, callback)
    }
    fun preTraverseDepth(callback : ((T, Int) -> Unit)) = _preTraverseDepth(this, 0, callback)
    fun _preTraverseDepth(node: SearchBinaryNode<T>?, depth: Int, callback : (T, Int) -> Unit){
        if (node == null)
            return
        callback(node.key, depth)
        _preTraverseDepth(node.left, depth + 1, callback)
        _preTraverseDepth(node.right, depth + 1, callback)
    }
    fun preTraverseParent(callback : (T, LeftOrRight, T?) -> Unit) = _preTraverseParent(this, LeftOrRight.NONE, null, callback)
    fun _preTraverseParent(node: SearchBinaryNode<T>?, lOrR: LeftOrRight, parent: SearchBinaryNode<T>?, callback : (T, LeftOrRight, T?) -> Unit){
        if (node == null)
            return
        callback(node.key, lOrR, parent?.key)
        _preTraverseParent(node.left, LeftOrRight.LEFT, node, callback)
        _preTraverseParent(node.right, LeftOrRight.RIGHT, node, callback)
    }
    fun inTraverse(callback : (T) -> Unit) = _inTraverse(this, callback)
    fun _inTraverse(node: SearchBinaryNode<T>?, callback : ((T) -> Unit)){
        if (node == null)
            return
        _inTraverse(node.left, callback)
        callback(node.key)
        _inTraverse(node.right, callback)
    }
    fun inTraverseR(callback : (T) -> Unit) = _inTraverseR(this, callback)
    fun _inTraverseR(node: SearchBinaryNode<T>?, callback : ((T) -> Unit)){
        if (node == null)
            return
        _inTraverseR(node.right, callback)
        callback(node.key)
        _inTraverseR(node.left, callback)
    }
    fun postTraverse(callback : (T) -> Unit) = _postTraverse(this, callback)
    fun _postTraverse(node: SearchBinaryNode<T>?, callback : ((T) -> Unit)){
        if (node == null)
            return
        _postTraverse(node.left, callback)
        _postTraverse(node.right, callback)
        callback(node.key)
    }
}
