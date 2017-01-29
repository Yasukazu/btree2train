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
interface SearchBinaryTree<T: Comparable<T>> {
    val size: Int
    operator fun contains(item: T): Boolean
    var root: SearchBinaryNode<T>?
    fun add(item: T)
    fun remove(item: T): DeleteResult

    fun preTraverse(callback: (T)->Unit){
        fun _traverse(node: SearchBinaryNode<T>?){
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
        val map: MutableMap<Int, MutableSet<T>> = TreeMap()
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
    fun rootNodeCopy(): SearchBinaryNode<T>?
    fun nodeInterface(item: T): SearchBinaryNode<T>?
    fun nodeCopy(item: T): SearchBinaryNode<T>?
}