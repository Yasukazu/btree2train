package jp.yasukazu.kotlin.train
/**
 * I18n text process
 * Created by Yasukazu on 2016/11/25.
 */
//import com.ibm.icu.text.Transliterator
import jp.yasukazu.kotlin.train.BinarySearchTree


import java.text.Collator
import java.text.CollationKey
import java.text.Normalizer
import java.util.*
//import kotlin.Comparator


/*
interface toStringable {
    fun toString():String
}*/
/* interface toTextable {
    fun toText():String
}*/
class KollationKey(val key: CollationKey):Comparable<KollationKey>{
    override operator fun compareTo(other: KollationKey): Int = key.compareTo(other.key)
    /*
    override fun toText(): String {
        return key.sourceString
    }*/
    override fun toString(): String {
        return key.sourceString
    }
}
/*
class CollatorString<T:Comparable<T>>:Comparable(val s:T){
    override fun toString():String{
        val k = s as CollationKey
        return k.sourceString
    }
}*/

fun main(args: Array<String>){
    fun CollationKey.toString():String{
        return sourceString
    }

    val collator = Collator.getInstance()//Locale.JAPANESE)
    collator.strength = Collator.SECONDARY
    //val trans = Transliterator.getInstance("Fullwidth-Halfwidth")//-Fullwidth")
    val root = BinarySearchTree<KollationKey>()
    val treeMap = TreeMap<KollationKey, String>()


    args.forEach { a ->
        println(a)
        val cKey = KollationKey(collator.getCollationKey(Normalizer.normalize((a), Normalizer.Form.NFKC)))
        root.insert(cKey)
        treeMap[cKey] = a
    }
    println()
    root.inTraverse { k ->
        println("${k}")
        return@inTraverse false
    }
    println()
    // Reverse: not as expected..{Kanji(not 50on jun), ANK, Hiragana}
    root.inTraverse(reverse = true){ k ->
        println("${k}")//{k.sourceString}")
        return@inTraverse false
    }
    println()
    for((k, v) in treeMap.entries){
        println("${k} : $v")
    }

}
