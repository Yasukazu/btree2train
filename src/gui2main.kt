/**
 * Created by Yasukazu on 2016/12/29.
 * GUI main outside from the package
 * 2016-12-29 BinarySearchTreeFrame (accepts General type)
 */
import jp.yasukazu.kotlin.train.BinarySearchTreeModel
import jp.yasukazu.kotlin.train.BinarySearchTreeFrame
import jp.yasukazu.kotlin.train.KollationKey
import jp.yasukazu.kotlin.train.NormalizerCollator
import java.awt.Rectangle
import java.io.FileOutputStream
import java.io.ObjectOutputStream
//import java.text.CollationKey
import java.text.Collator
import java.text.Normalizer
import java.util.*
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.SwingUtilities
import kotlin.reflect.jvm.internal.pcollections.HashPMap


fun main(args:Array<String>){
    val floatTreeModel = BinarySearchTreeModel<Float>()
    val stringTreeModel = BinarySearchTreeModel<String>()
    val kollatorTreeModel = BinarySearchTreeModel<KollationKey>()
    val normalizerCollator = NormalizerCollator()
    //data class ComparablePairTreeModel<T:Comparable<T>>(val key: class, val model: BinarySearchTreeModel<T> )
    val treeModelMap = mapOf(Pair(Float::class, floatTreeModel), Pair(String::class, stringTreeModel), Pair(KollationKey::class, kollatorTreeModel))
    args.forEach { a ->
        treeModelMap.forEach {
            val (k, model) = it
            when(k){
                Float::class ->
                    try {
                        floatTreeModel.insert(a.toFloat())
                    } catch (e: NumberFormatException){
                        println("$a caused $e")
                    }
                String::class -> stringTreeModel.insert(a)
                KollationKey::class -> kollatorTreeModel.insert(normalizerCollator[a])
            }
        }
    }
    /*
    fun fromStringToKollationKey(it: String): KollationKey {
        val collator = Collator.getInstance()//Locale.JAPANESE)
        collator.strength = Collator.SECONDARY
        return KollationKey(collator.getCollationKey(Normalizer.normalize((it), Normalizer.Form.NFKC)))
    } */

    SwingUtilities.invokeLater {
        val intTreeFrame = BinarySearchTreeFrame(floatTreeModel){it.toFloat()} // catch (e:NumberFormatException){null}}
        with(intTreeFrame){
            /*
 val serializeButton = JButton("Serialize to $filename")
 serializeButton.addActionListener {
 val list = kollatorTreeModel.preTraverseList()
 val filename = "___outputStream___.$$$"

     FileOutputStream(filename).use { output ->
         ObjectOutputStream(output).use { oo ->
             oo.writeObject(list)
         }
     }
 }
 entryPanel.add(serializeButton) */
            title = "Real Number Tree"
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            setBounds(0, 0, 400, 600)
            pack()
            isVisible = true
        }
        val stringTreeFrame = BinarySearchTreeFrame(stringTreeModel){it}
        with(stringTreeFrame){
            title = "String Tree"
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            setBounds(50, 50, 400, 600)
            pack()
            isVisible = true
        }
        val kollationKeyFrame = BinarySearchTreeFrame(kollatorTreeModel) { normalizerCollator[it] }//::fromStringToKollationKey)
        with(kollationKeyFrame){

            title = "Collation String Tree"
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            setBounds(100, 100, 400, 600)
            pack()
            isVisible = true
        }
    }
}

