package jp.yasukazu.kotlin.train

/**
 * delegation train: interface variant out|in by
 * Created by Yasukazu on 2017/01/02.
 */

class SizedBinarySearchTree<in T: Comparable<T>>(private val tree: BinarySearchTree<T>) : InsertDeletable<T> by tree {
    var size: Int = 0
        private  set

    override fun insert(i: T): InsertedPos{
        val result = tree.insert(i)
        if(result != InsertedPos.NONE)
            size++
        return result
    }

    override fun delete(i: T): DeleteResult{
        val result = tree.delete(i)
        if (result != DeleteResult.EMPTY || result != DeleteResult.NO_MATCH)
            size--
        return result
    }
}

fun main(args: Array<String>){
    val tree = SizedBinarySearchTree(BinarySearchTree<Int>())
    println("Size: ${tree.size}")
    tree.insert(5)
    println("Size: ${tree.size}")
    tree.delete(5)
    println("Size: ${tree.size}")
}
