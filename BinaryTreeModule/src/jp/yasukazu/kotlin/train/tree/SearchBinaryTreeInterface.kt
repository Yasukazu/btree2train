package jp.yasukazu.kotlin.train.tree

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
    val root: SearchBinaryNodeInterface<T>?
    fun add(item: T)
    fun remove(item: T, callback: ((DeleteResult)->Unit)?=null)
    fun preTraverse(callback: (T)->Unit)
    fun inTraverse(callback: (T)->Unit)
    fun postTraverse(callback: (T)->Unit)
    fun rootNodeCopy(): SearchBinaryNode<T>?
    fun nodeInterface(item: T): SearchBinaryNodeInterface<T>?
    fun nodeCopy(item: T): SearchBinaryNode<T>?
}