package jp.yasukazu.kotlin.train.tree

import java.util.*

/**
 * isolate interface
 * Created by Yasukazu on 2017/01/23.
 */
/**
 * Created by Yasukazu on 2017/01/20.
 *
 */
interface SearchBinaryTreeInterface<T: Comparable<T>> {
    val size: Int
    operator fun contains(item: T): Boolean
    var root: SearchBinaryNodeInterface<T>?
    fun add(item: T)
    fun remove(item: T): DeleteResult {
        var result = DeleteResult.EMPTY
        if (root != null) {
            try {
                _delete_node(item, root, null)
            }
            catch(dE: DeleteException){
                if (dE is DeleteSuccessException) {
                    result = dE.result
                } else
                    throw dE
            }
        }
        return result
    }
    tailrec fun _delete_node(item: T, self: SearchBinaryNodeInterface<T>?, parent: SearchBinaryNodeInterface<T>?){
        // Delete self binaryNode
        fun __delete_self_node(self: SearchBinaryNodeInterface<T>, parent: SearchBinaryNodeInterface<T>?){
            assert(self.left == null && self.right == null)
            if (parent == null)
                root = null
            else {
                if (parent.left == self)
                    parent.left = null
                else if (parent.right == self)
                    parent.right = null
                else
                    assert(true, {"Unable to delete self: Parent has no self!"})
            }
        }
        // replace self with 1 child
        fun __replace(self: SearchBinaryNodeInterface<T>, child: SearchBinaryNodeInterface<T>, parent: SearchBinaryNodeInterface<T>?){
            if (parent == null) {
                root = child
            } else {
                if (parent.left == self)
                    parent.left = child
                else if (parent.right == self)
                    parent.right = child
                else
                    assert(true, {"Unable to replace self with a child: Parent has no self!"})
            }
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
                return _delete_node(self.left!!.key, self, parent)
            } else if (item > self.key) {
                return _delete_node(self.right!!.key, self, parent)
            } else { // item == self.item
                // 3. delete self if no iterCount
                val bL = if(self.left == null) 0 else 1
                val bR = if(self.right == null) 0 else 2
                when(bL or bR){
                    0 -> {
                        __delete_self_node(self, parent)
                        throw DeleteSuccessException(DeleteResult.SELF_DELETE)
                    }
                    1 -> {
                        __replace(self, self.left!!, parent)
                        throw DeleteSuccessException(DeleteResult.LEFT_REPLACE)
                    }
                    2 -> {
                        __replace(self, self.right!!, parent)
                        throw DeleteSuccessException(DeleteResult.RIGHT_REPLACE)
                    }
                    else -> {
                        __replace2(self)
                        throw DeleteSuccessException(DeleteResult.PREDEC_REPLACE)
                    }
                }
            }
        }
    }
    fun preTraverse(callback: (T)->Unit){
        fun _traverse(node: SearchBinaryNodeInterface<T>?){
            if (node == null)
                return
            else {
                callback(node.key)
                _traverse(node.left)
                _traverse(node.right)
            }
        }
        _traverse(root)
    }
    fun preTraverseDepth(callback : ((T, Int) -> Unit)) {
        if(root != null)
            root!!._preTraverseDepth(root, 0, callback)
    }
    fun preTraverseDepthMap(): Map<Int, Set<T>> {
        val map: MutableMap<Int, MutableSet<T>> = HashMap()
        if(root != null)
            root!!._preTraverseDepth(root, 0){key, depth ->
               if (depth in map) {
                   map[depth]!!.add(key)
               }
                else {
                   map.put(depth, mutableSetOf(key))
               }
            }
        return map
    }
    fun inTraverse(callback: (T)->Unit)
    fun postTraverse(callback: (T)->Unit)
    fun rootNodeCopy(): SearchBinaryNodeInterface<T>?
    fun nodeInterface(item: T): SearchBinaryNodeInterface<T>?
    fun nodeCopy(item: T): SearchBinaryNodeInterface<T>?
}