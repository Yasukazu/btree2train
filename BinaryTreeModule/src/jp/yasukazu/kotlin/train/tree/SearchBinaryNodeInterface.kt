package jp.yasukazu.kotlin.train.tree

/**
 *
 * Created by Yasukazu on 2017/01/22.
 */

enum class InsertedPos { LEFT, NEW, RIGHT, NONE }

/**
 * Node Interface is only about self, right and left
 * includes recursive properties/functions
 */
class _FoundException(val found: Boolean) : Exception()//, val node: SearchBinaryNodeInterface<T>?, val parent: SearchBinaryNodeInterface<T>?): Exception()
interface SearchBinaryNodeInterface<T: Comparable<T>>{
    var key: T
    var left: SearchBinaryNodeInterface<T>?
    var right: SearchBinaryNodeInterface<T>?
    fun add(item: T, callback: ((InsertedPos)->Unit)?)
    tailrec fun _find(item: T, node: SearchBinaryNodeInterface<T>?, parent: SearchBinaryNodeInterface<T>?, callback: ((SearchBinaryNodeInterface<T>?, SearchBinaryNodeInterface<T>?)->Unit)?){
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
    fun findNode(item: T): Pair<SearchBinaryNodeInterface<T>?, SearchBinaryNodeInterface<T>?> {
        var node: SearchBinaryNodeInterface<T>? = null
        var parent: SearchBinaryNodeInterface<T>? = null
        try {
            _find(item, this, null) { n, p -> node = n
                parent = p}
        } catch (ex: _FoundException){
            if (ex.found)
                return Pair(node, parent)
        }
        return Pair(null, null)
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
