package io.gitlab.arturbosch.detekt

import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import io.gitlab.arturbosch.detekt.config.DetektConfigStorage
import org.intellij.detekt.api.DiaFinding
import org.intellij.detekt.api.DiaTextLocation
import org.intellij.detekt.bridge.DetektBridge

/**
 * @author Dmytro Primshyts
 * @author Artur Bosch
 */
class DetektAnnotator : ExternalAnnotator<PsiFile, List<DiaFinding>>() {

  override fun collectInformation(file: PsiFile): PsiFile = file

  override fun doAnnotate(collectedInfo: PsiFile): List<DiaFinding> {
    // FIXME Triggering of save document must be removed!!!
    WriteCommandAction.runWriteCommandAction(collectedInfo.project, Computable<Boolean> {
      val documentManager = FileDocumentManager.getInstance()
      val document = documentManager.getDocument(collectedInfo.virtualFile)
      if (document != null) {
        documentManager.saveDocument(document)
        return@Computable false
      }
      true
    })

    val configuration = DetektConfigStorage.instance(collectedInfo.project)
    if (configuration.enableDetekt) {
      return if (ProjectRootsUtil.isInTestSource(collectedInfo)
          && !configuration.checkTestFiles
      ) {
        emptyList()
      } else {
        runDetekt(collectedInfo, configuration)
      }
    }

    return emptyList()
  }

  private fun runDetekt(
      collectedInfo: PsiFile,
      configuration: DetektConfigStorage
  ): List<DiaFinding> {
    val virtualFile = collectedInfo.originalFile.virtualFile

    val result = DetektBridge(
        virtualFile.path,
        DetektConfigStorage.toPluginConfiguration(collectedInfo.project)
    ).execute()

    return result.findings.flatMap { it.value }
  }

  override fun apply(
      file: PsiFile,
      annotationResult: List<DiaFinding>,
      holder: AnnotationHolder
  ) {
    val configuration = DetektConfigStorage.instance(file.project)
    annotationResult.forEach {
      val textRange = it.charPosition.toTextRange()
      val message = it.id + ": " + it.messageOrDescription
      if (configuration.treatAsError) {
        holder.createErrorAnnotation(textRange, message)
      } else {
        holder.createWarningAnnotation(textRange, message)
      }
    }
  }

  private fun DiaTextLocation.toTextRange(): TextRange = TextRange.create(start, end)

}
