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
interface SearchBinaryNodeInterface<T: Comparable<T>>{
    var key: T
    var left: SearchBinaryNodeInterface<T>?
    var right: SearchBinaryNodeInterface<T>?
    fun add(item: T, callback: ((InsertedPos)->Unit)?)
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
