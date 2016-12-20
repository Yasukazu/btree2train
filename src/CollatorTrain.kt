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
    //val trans = Transliterator.getInstance("Fullwidth-Halfwidth")//-Fullwidth")
    val root = BinarySearchTree<CollationKey>()
    val treeMap = TreeMap<CollationKey, String>()

    args.forEach { a ->
        println(a)
        val cKey = collator.getCollationKey(Normalizer.normalize((a), Normalizer.Form.NFKC)) // collator.getCollationKey(t
        root.insert(cKey)
        treeMap[cKey] = a
    }
    println()
    root.inTraverse { k ->
        println("${k.sourceString}")
        return@inTraverse false
    }
    println()
    // Reverse: not as expected..{Kanji(not 50on jun), ANK, Hiragana}
    root.inTraverse(reverse = true, callback = { k ->
        println("${k.sourceString}")
        return@inTraverse false
    })
    println()
    for((k, v) in treeMap.entries){
        println("${k.sourceString} : $v")
    }

}
