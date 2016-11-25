/**
 * Created by Yasukazu on 2016/11/25.
 */
import com.ibm.icu.text.Transliterator
import jp.yasukazu.kotlin.train.BinarySearchTree


import java.text.Collator
import java.text.CollationKey
import java.util.Locale
import java.text.Normalizer

fun main(args: Array<String>){
    val collator = Collator.getInstance(Locale.JAPANESE)
    collator.strength = Collator.PRIMARY
    val colKeyList = mutableListOf<CollationKey>()
    val trans = Transliterator.getInstance("Fullwidth-Halfwidth")

    args.forEach { a ->
        println(a)
        val n = Normalizer.normalize(a, Normalizer.Form.NFC)
        val t = trans.transliterate(n)
        colKeyList.add(collator.getCollationKey(t))
    }
    println()
    val root = BinarySearchTree<CollationKey>()
    colKeyList.forEach { k ->
        root.insert(k)
    }
    root.inTraverse { k ->
        println("${k.sourceString}")
    }
}
