package com.application.newDiscussionForum.discussion.ui

import android.graphics.Color
import android.os.Bundle
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobcast.R
import com.mobcast.databinding.EmployeeListItemBinding
import com.mobcast.databinding.FragmentCommentBinding
import com.mobcast.discussion.DiscussionReplyAdapter
import com.mobcast.discussion.models.DiscussionReply
import com.mobcast.discussion.models.Employee
import timber.log.Timber


class DiscussionReplyFragment : BottomSheetDialogFragment() {

    private lateinit var _binding: FragmentCommentBinding
    private val binding: FragmentCommentBinding get() = _binding
    private val discussionReplyAdapter = DiscussionReplyAdapter(true)
    private val adapterList = mutableListOf<Employee>()
    private val taggedEmployeeMap = mutableMapOf<String, Employee>()
    private var beforeTextToStoreBefore = ""
    private var beforeTextToStoreAfter = ""
    private var afterTextToStoreBefore = ""
    private var afterTextToStoreAfter = ""
    private var adapterFilterSearchIndex = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = FragmentCommentBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        arguments?.getSerializable("replies")?.let {
            binding.replyRV.apply {
                adapter = discussionReplyAdapter
                discussionReplyAdapter.submitList(it as List<DiscussionReply>)
                layoutManager = LinearLayoutManager(requireContext()).apply {
                    orientation = RecyclerView.VERTICAL
                }
            }
        }

        arguments?.getSerializable("employees")?.let {
            it as List<Employee>
            if (it.isNotEmpty()) {
                adapterList.clear()
                adapterList.addAll(it)
                val employeeFilter = object : Filter() {
                    private val filteredList = mutableListOf<Employee>()
                    private val filterResults = FilterResults()
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        filteredList.clear()
                        if (constraint == null || constraint.isEmpty()) {
                            filteredList.addAll(it)
                        } else {
                            if (binding.addReplyText.selectionStart != -1) {
                                val selectedText = binding.addReplyText.text.substring(
                                    0,
                                    binding.addReplyText.selectionStart
                                )
                                for (index in selectedText.indices) {
                                    if (selectedText[selectedText.length - 1 - index] == ' ') {
                                        break
                                    } else if (selectedText[selectedText.length - 1 - index] == '@') {
                                        adapterFilterSearchIndex = selectedText.length - 1 - index
                                        break
                                    }
                                }
                                if (adapterFilterSearchIndex != -1) {
                                    val searchPattern =
                                        if (adapterFilterSearchIndex == selectedText.length - 1) {
                                            ""
                                        } else {
                                            selectedText.substring(adapterFilterSearchIndex + 1)
                                                .lowercase().trim()
                                        }
                                    for (employee in it) {
                                        employee.employeeName?.let { employeeName ->
                                            if (employeeName.lowercase().trim()
                                                    .startsWith(searchPattern)
                                            ) {
                                                filteredList.add(employee)
                                            }
                                        }
                                    }
                                }
                            } else {
                                for (index in constraint.indices) {
                                    if (constraint[constraint.length - 1 - index] == ' ') {
                                        break
                                    } else if (constraint[constraint.length - 1 - index] == '@') {
                                        adapterFilterSearchIndex = constraint.length - 1 - index
                                        break
                                    }
                                }
                                if (adapterFilterSearchIndex != -1) {
                                    val searchPattern =
                                        if (adapterFilterSearchIndex == constraint.length - 1) {
                                            ""
                                        } else {
                                            constraint.substring(adapterFilterSearchIndex + 1)
                                                .lowercase().trim()
                                        }
                                    for (employee in it) {
                                        employee.employeeName?.let { employeeName ->
                                            if (employeeName.lowercase().trim()
                                                    .startsWith(searchPattern)
                                            ) {
                                                filteredList.add(employee)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        filterResults.count = filteredList.size
                        filterResults.values = filteredList
                        return filterResults
                    }

                    override fun publishResults(
                        constraint: CharSequence?,
                        results: FilterResults?
                    ) {
                        results?.values?.let {
                            adapterList.clear()
                            adapterList.addAll(results.values as List<Employee>)
                        }
                        (binding.addReplyText.adapter as ArrayAdapter<Employee>).notifyDataSetChanged()
                    }

                }
                binding.addReplyText.setAdapter(object : ArrayAdapter<Employee>(
                    requireContext(),
                    R.layout.employee_list_item,
                    adapterList
                ) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val employeeItemListBinding =
                            EmployeeListItemBinding.inflate(layoutInflater)
                        getItem(position)?.let { employee ->
                            employee.profilePicture?.let { profileUrl ->
                                employeeItemListBinding.employeeImage.load(profileUrl)
                            }
                            employee.employeeName?.let { employeeName ->
                                employeeItemListBinding.employeeName.text = employeeName
                            }
                        }
                        return employeeItemListBinding.root
                    }

                    override fun getFilter(): Filter {
                        return employeeFilter
                    }
                })
                binding.addReplyText.threshold = 1
                binding.addReplyText.doOnTextChanged { text, _, _, _ ->
                    if (!text.isNullOrEmpty()) {
                        if (binding.addReplyText.selectionStart != -1) {
                            beforeTextToStoreBefore = beforeTextToStoreAfter
                            afterTextToStoreBefore = afterTextToStoreAfter
                            val selectedText = binding.addReplyText.text.subSequence(
                                0,
                                binding.addReplyText.selectionStart
                            )
                            for (index in selectedText.indices) {
                                if (selectedText[selectedText.length - 1 - index] == '@') {
                                    beforeTextToStoreBefore = beforeTextToStoreAfter
                                    if (index == selectedText.length - 1) {
                                        beforeTextToStoreAfter = ""
                                        break
                                    } else {
                                        beforeTextToStoreAfter =
                                            selectedText.substring(
                                                0,
                                                selectedText.length - 1 - index
                                            )
                                        afterTextToStoreAfter =
                                            if (binding.addReplyText.selectionStart == text.length) {
                                                ""
                                            } else {
                                                text.substring(binding.addReplyText.selectionStart)
                                            }
                                        break
                                    }
                                } else if (selectedText[selectedText.length - 1 - index] == ' ') {
                                    break
                                }
                            }
                            val selectedTextForRemoval = binding.addReplyText.text.substring(
                                0,
                                binding.addReplyText.selectionStart
                            )
                            var start = -1
                            for (index in selectedTextForRemoval.indices) {
                                if (selectedTextForRemoval[selectedTextForRemoval.length - 1 - index] == ' ') {
                                    break
                                } else if (selectedTextForRemoval[selectedTextForRemoval.length - 1 - index] == '@') {
                                    start = selectedTextForRemoval.length - 1 - index
                                    break
                                }
                            }
                            adapterFilterSearchIndex = start
                            if (start != -1 && start != binding.addReplyText.text.length - 1) {
                                val span = binding.addReplyText.text.getSpans(
                                    start,
                                    binding.addReplyText.selectionStart,
                                    ForegroundColorSpan::class.java
                                )
                                if (span.isNotEmpty()) {
                                    if (((start != binding.addReplyText.selectionStart - 1) && taggedEmployeeMap[span[0].toString()]!!.employeeName!!.substring(
                                            0,
                                            taggedEmployeeMap[span[0].toString()]!!.employeeName!!.length - 1
                                        ).replace(" ", "_") == binding.addReplyText.text.substring(
                                            start + 1,
                                            binding.addReplyText.selectionStart
                                        )
                                                )
                                    ) {
                                        binding.addReplyText.text.removeSpan(span[0])
                                        binding.addReplyText.text.replace(
                                            start,
                                            binding.addReplyText.selectionStart, ""
                                        )
                                        taggedEmployeeMap.remove(span[0].toString())
                                    } else if (start != binding.addReplyText.selectionStart - 1 && taggedEmployeeMap[span[0].toString()]!!.employeeName!!.substring(
                                            0,
                                            taggedEmployeeMap[span[0].toString()]!!.employeeName!!.length - 1
                                        ).replace(
                                            " ",
                                            ""
                                        ).startsWith(
                                            binding.addReplyText.text.substring(
                                                start + 1,
                                                binding.addReplyText.selectionStart
                                            ).replace("_", "")
                                        )
                                    ) {

                                        binding.addReplyText.text.removeSpan(span[0])
                                        binding.addReplyText.text.replace(
                                            start,
                                            start + taggedEmployeeMap[span[0].toString()]!!.employeeName!!.substring(
                                                0,
                                                taggedEmployeeMap[span[0].toString()]!!.employeeName!!.length - 1
                                            ).length + 1,
                                            ""
                                        )
                                        taggedEmployeeMap.remove(span[0].toString())
                                    }
                                }
                            }
                        } else {
                            taggedEmployeeMap.clear()
                        }

                    }
                }
                binding.addReplyText.setOnItemClickListener { _, _, position, _ ->
                    val employeeName =
                        (binding.addReplyText.adapter as ArrayAdapter<Employee>).getItem(position)!!.employeeName!!
                    val newSelectionIndex =
                        "%s@%s".format(beforeTextToStoreBefore, employeeName).length + 1

                    if(afterTextToStoreBefore.startsWith(' ')) {

                        binding.addReplyText.setText(
                            "%s@%s%s ".format(
                                beforeTextToStoreBefore,
                                employeeName.replace(" ", "_"),
                                afterTextToStoreBefore
                            )
                        )
                    } else {
                        binding.addReplyText.setText(
                            "%s@%s %s ".format(
                                beforeTextToStoreBefore,
                                employeeName.replace(" ", "_"),
                                afterTextToStoreBefore
                            )
                        )
                    }


                    var i = 0
                    val employeeList = taggedEmployeeMap.values.toList()
                    val existingSize = taggedEmployeeMap.size
                    taggedEmployeeMap.clear()
                    while (i < binding.addReplyText.text.length) {
                        if (binding.addReplyText.text[i] == '@') {
                            val start = i
                            i++
                            while (i < binding.addReplyText.text.length && (binding.addReplyText.text[i] != ' ' && binding.addReplyText.text[i] != '@')) {
                                i++
                            }
                            if ((start + 1) < binding.addReplyText.text.length) {
                                var toLoop = true
                                for (employee in it) {
                                    employee.employeeName?.let { employeeNameToMatch ->
                                        if (employeeNameToMatch.replace(
                                                " ",
                                                "_"
                                            ) == binding.addReplyText.text.substring(start + 1, i)
                                        ) {
                                            binding.addReplyText.text.setSpan(
                                                ForegroundColorSpan(Color.parseColor("#DCABAB")),
                                                start,
                                                i,
                                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                            )
                                            val span = binding.addReplyText.text.getSpans(
                                                start,
                                                i,
                                                ForegroundColorSpan::class.java
                                            )[0]
                                            if (taggedEmployeeMap.size < existingSize) {
                                                taggedEmployeeMap[span.toString()] =
                                                    employeeList[taggedEmployeeMap.size]
                                            } else {
                                                Timber.e(span.toString())
                                                taggedEmployeeMap[span.toString()] =
                                                    (binding.addReplyText.adapter as ArrayAdapter<Employee>).getItem(
                                                        position
                                                    )!!
                                            }
                                            toLoop = false
                                        }

                                    }
                                    if (!toLoop) {
                                        break
                                    }
                                }
                            }

                        } else {
                            i++
                        }
                    }
                    binding.addReplyText.setSelection(newSelectionIndex)
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(replies: ArrayList<DiscussionReply>, employees: ArrayList<Employee>) =
            DiscussionReplyFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("replies", replies)
                    putSerializable("employees", employees)
                }
            }
    }
}