package jp.yasukazu.kotlin.train.tree

/**
 * Interface and read-only functions
 * Created by Yasukazu on 2017/01/22.
 */

interface SearchBinaryNode<T: Comparable<T>>{
    val key: T
    //fun setKey(newKey: T)
    val left: SearchBinaryNode<T>? // set() must have restriction
    val right: SearchBinaryNode<T>?
    fun new(key: T): SearchBinaryNode<T> // psudo constructor
    /**
     * remove
     * @return : Pair of delete result and parental node
     */
    fun remove(item: T): DeleteResult

    fun add(item: T): InsertedPos


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
    fun findNode(item: T): Triple<Boolean, LeftOrRight, SearchBinaryNode<T>?> {
        var leftOrRight: LeftOrRight = LeftOrRight.NONE
        var parent: SearchBinaryNode<T>? = null
        var found = false
        try {
            _find(item, this, null) { n, p ->
                parent = p
                if (p != null) {
                    leftOrRight = if (p.left == n) LeftOrRight.LEFT else LeftOrRight.RIGHT
                }
            }
        } catch (ex: _FoundException){
            found = ex.found
        }
        return Triple(found, leftOrRight, parent)
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
    fun preTraverse(callback : (T) -> Unit) = _traverse(this, callback)
    fun preTraverseDepth(callback : ((T, Int) -> Unit)) = _preTraverseDepth(this, 0, callback)
    fun _preTraverseDepth(node: SearchBinaryNode<T>?, depth: Int, callback : (T, Int) -> Unit){
        if (node == null)
            return
        callback(node.key, depth)
        _preTraverseDepth(node.left, depth + 1, callback)
        _preTraverseDepth(node.right, depth + 1, callback)
    }
    enum class LeftOrRight {NONE, LEFT, RIGHT}
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
