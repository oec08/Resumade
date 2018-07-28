package com.haroldadmin.kshitijchauhan.resumade.adapter

import android.support.design.button.MaterialButton
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.haroldadmin.kshitijchauhan.resumade.R
import com.haroldadmin.kshitijchauhan.resumade.repository.database.Education
import com.haroldadmin.kshitijchauhan.resumade.utilities.DeleteButtonClickListener
import com.haroldadmin.kshitijchauhan.resumade.utilities.EditButtonClickListener
import com.haroldadmin.kshitijchauhan.resumade.utilities.SaveButtonClickListener
import com.haroldadmin.kshitijchauhan.resumade.utilities.showKeyboard
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class EducationAdapter(val saveButtonClickListener: SaveButtonClickListener,
                       val deleteButtonClickListener: DeleteButtonClickListener,
                       val editButtonClickListener: EditButtonClickListener) : RecyclerView.Adapter<EducationAdapter.EducationViewHolder>() {

	private var educationList: List<Education> = emptyList()

	override fun onCreateViewHolder(parent: ViewGroup, position: Int): EducationViewHolder {
		return EducationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_education, parent, false))
	}

	override fun getItemCount(): Int = educationList.size

	override fun onBindViewHolder(holder: EducationViewHolder, position: Int) {
		val education = educationList[position]
		holder.apply {
			setItem(education)
			bindClick()
		}
	}

	inner class EducationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		private lateinit var mEducation: Education

		private val instituteNameWrapper: TextInputLayout = itemView.findViewById(R.id.educationInstituteNameWrapper)
		private val instituteName: TextInputEditText = itemView.findViewById(R.id.educationInstituteName)
		private val degreeWrapper: TextInputLayout = itemView.findViewById(R.id.educationDegreeWrapper)
		private val degree: TextInputEditText = itemView.findViewById(R.id.educationDegree)
		private val performanceWrapper: TextInputLayout = itemView.findViewById(R.id.educationPerformanceWrapper)
		private val performance: TextInputEditText = itemView.findViewById(R.id.educationPerformance)
		private val yearWrapper: TextInputLayout = itemView.findViewById(R.id.educationYearWrapper)
		private val yearOfGraduation: TextInputEditText = itemView.findViewById(R.id.educationYear)
		private val saveButton: MaterialButton = itemView.findViewById(R.id.educationSaveButton)
		private val deleteButton: MaterialButton = itemView.findViewById(R.id.educationDeleteButton)
		private val editButton: MaterialButton = itemView.findViewById(R.id.educationEditButton)

		fun setItem(education: Education) {
			mEducation = education
			this.apply {
				instituteName.setText(mEducation.instituteName)
				degree.setText(mEducation.degree)
				performance.setText(mEducation.performance)
				yearOfGraduation.setText(mEducation.year)
				saveButton.apply {
					if (mEducation.saved) {
						isEnabled = false
						text = "Saved"
					} else {
						isEnabled = true
						text = "Save"
					}
				}
				editButton.isEnabled = mEducation.saved

				instituteNameWrapper.isEnabled = !mEducation.saved
				degreeWrapper.isEnabled = !mEducation.saved
				performanceWrapper.isEnabled = !mEducation.saved
				yearWrapper.isEnabled = !mEducation.saved
			}
		}

		fun bindClick() {
			saveButton.apply {
				setOnClickListener {
					val instituteName = this@EducationViewHolder.instituteName.text?.toString()
							?: ""
					val degree = this@EducationViewHolder.degree.text?.toString() ?: ""
					val performance = this@EducationViewHolder.performance.text?.toString() ?: ""
					val year = this@EducationViewHolder.yearOfGraduation.text?.toString() ?: ""

					var passed = true

					if (instituteName.trim().isEmpty()) {
						instituteNameWrapper.error = "Please enter an institute name"
						passed = false
					} else {
						instituteNameWrapper.isErrorEnabled = false
					}
					if (degree.trim().isEmpty()) {
						degreeWrapper.error = "Can't be empty"
						passed = false
					} else {
						degreeWrapper.isErrorEnabled = false
					}
					if (performance.trim().isEmpty()) {
						performanceWrapper.error = "Can't be empty"
						passed = false
					} else {
						performanceWrapper.isErrorEnabled = false
					}
					if (year.trim().toLongOrNull() == null) {
						yearWrapper.error = "Year must contain numbers only"
						passed = false
					} else if (year.trim().isEmpty()) {
						yearWrapper.error = "Please enter the graduation year"
						passed = false
					} else {
						yearWrapper.isErrorEnabled = false
					}

					if (passed) {
						// Save the new values into the member variable
						mEducation.instituteName = instituteName
						mEducation.degree = degree
						mEducation.performance = performance
						mEducation.year = year
						saveButtonClickListener.onSaveButtonClick(mEducation)

						// Enable edit button and disable save button
						isEnabled = false
						text = "Saved"
						editButton.isEnabled = true

						// Disable text fields
						instituteNameWrapper.isEnabled = false
						degreeWrapper.isEnabled = false
						performanceWrapper.isEnabled = false
						yearWrapper.isEnabled = false
					}
				}
			}
			deleteButton.setOnClickListener {
				/*
				I love anko-dialogs.
				 */
				itemView.context.alert(Appcompat, "Are you sure you want to delete this education card?") {
					yesButton {
						deleteButtonClickListener.onDeleteButtonClick(mEducation)
					}
					noButton { /* Do Nothing */ }
				}.show()
			}

			editButton.setOnClickListener {
				editButtonClickListener.onEditButtonClicked(mEducation)

				// Enable text fields
				instituteNameWrapper.apply {
					isEnabled = true
					requestFocus()
					showKeyboard(itemView.context)
				}
				degreeWrapper.isEnabled = true
				performanceWrapper.isEnabled = true
				yearWrapper.isEnabled = true

				it.isEnabled = false
				saveButton.isEnabled = true
				saveButton.text = "Save"
			}
		}
	}

	fun updateEducationList(newEducationList: List<Education>) {
		val educationDiffUtilCallback = DiffUtilCallback(this.educationList, newEducationList)
		val diffResult = DiffUtil.calculateDiff(educationDiffUtilCallback)
		educationList = newEducationList
		diffResult.dispatchUpdatesTo(this)
	}

}