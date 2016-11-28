/**
 * Created by Yasukazu on 2016/11/25.
 */
import com.ibm.icu.text.Transliterator
import jp.yasukazu.kotlin.train.BinarySearchTree


import java.text.Collator
import java.text.CollationKey
import java.text.Normalizer
import java.util.*

fun main(args: Array<String>){
    val collator = Collator.getInstance(Locale.JAPANESE)
    collator.strength = Collator.SECONDARY
    val trans = Transliterator.getInstance("Fullwidth-Halfwidth")//-Fullwidth")
    val root = BinarySearchTree<CollationKey>()
    val treeMap = TreeMap<CollationKey, String>()

    args.forEach { a ->
        println(a)
        val cKey = collator.getCollationKey(Normalizer.normalize(trans.transliterate(a), Normalizer.Form.NFKC))
        root.insert(cKey)
        treeMap[cKey] = a
    }
    println()
    root.inTraverse { k ->
        println("${k.sourceString}")
    }
    println()
    for((k, v) in treeMap.entries){
        println("${k.sourceString} : $v")
    }

}
