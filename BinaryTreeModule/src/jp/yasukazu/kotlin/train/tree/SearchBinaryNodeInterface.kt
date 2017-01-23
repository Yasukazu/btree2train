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



    fun add(item: T, callback: ((InsertedPos)->Unit)?=null){
        //try {
        val (found, node, parent) = findNode(item)
        if (found)
            throw InsertFailException()
        assert(node == null) {"Node must be null!"}
        assert(parent != null) {"Parent must be non-null!"}
        if (item < key) {
           parent!!.left = new(item)
           if (callback != null)
               callback(InsertedPos.LEFT)
        }
        else {
           parent!!.right = new(item)
           if (callback != null)
               callback(InsertedPos.RIGHT)
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
    operator fun contains(item: T): Boolean // at most 3 members
    fun isLeaf() = left == null && right == null
    fun childCount() = (if (left != null) 1 else 0) + (if(right != null) 1 else 0)
    fun memberCount() = childCount() + 1
    fun childStatus() = (if (left != null) 1 else 0) or (if(right != null) 2 else 0)
    val childrenStatus: Int get() = (if (left != null) 1 else 0) + (if (right != null) 2 else 0)
    val min: T
    val max: T
    val total: Int get() { return count()}

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
