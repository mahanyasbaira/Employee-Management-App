package com.vishwaagrotech.digitalhr.repository.network

import com.vishwaagrotech.digitalhr.repository.network.api.GlobalResponse
import com.vishwaagrotech.digitalhr.repository.network.api.attentancestatus.AttendanceStatusResponse
import com.vishwaagrotech.digitalhr.repository.network.api.changepassword.ChangePasswordResponse
import com.vishwaagrotech.digitalhr.repository.network.api.commentlistresponse.CommentListResponse
import com.vishwaagrotech.digitalhr.repository.network.api.companyrules.CompanyRulesResponse
import com.vishwaagrotech.digitalhr.repository.network.api.createtadaresponse.CreateTadaResponse
import com.vishwaagrotech.digitalhr.repository.network.api.dashboard.DashboardResponse
import com.vishwaagrotech.digitalhr.repository.network.api.employeeattendancereport.EmployeeAttandanceReportResponse
import com.vishwaagrotech.digitalhr.repository.network.api.employeedetailresponse.EmployeeDetailResponse
import com.vishwaagrotech.digitalhr.repository.network.api.employeeleavecalendar.EmployeeLeaveCalendarResponse
import com.vishwaagrotech.digitalhr.repository.network.api.employeeleavecalendarbyday.EmployeeLeaveCalendarByDayResponse
import com.vishwaagrotech.digitalhr.repository.network.api.holiday.HolidayResponse
import com.vishwaagrotech.digitalhr.repository.network.api.leaverequest.LeaveRequestResponse
import com.vishwaagrotech.digitalhr.repository.network.api.leaverequestlist.LeaveRequestListResponse
import com.vishwaagrotech.digitalhr.repository.network.api.leavetype.LeaveTypeResponse
import com.vishwaagrotech.digitalhr.repository.network.api.login.LoginResponse
import com.vishwaagrotech.digitalhr.repository.network.api.logout.LogoutResponse
import com.vishwaagrotech.digitalhr.repository.network.api.notice.NoticeResponse
import com.vishwaagrotech.digitalhr.repository.network.api.notification.NoticationResponse
import com.vishwaagrotech.digitalhr.repository.network.api.projectdashboardresponse.ProjectDashboardReponse
import com.vishwaagrotech.digitalhr.repository.network.api.projectdetailresponse.ProjectDetailResponse
import com.vishwaagrotech.digitalhr.repository.network.api.projectlistresponse.ProjectListResponse
import com.vishwaagrotech.digitalhr.repository.network.api.savecommentresponse.SaveCommentResponse
import com.vishwaagrotech.digitalhr.repository.network.api.savesupportresponse.SupportSaveResponse
import com.vishwaagrotech.digitalhr.repository.network.api.staticpage.StaticPageResponse
import com.vishwaagrotech.digitalhr.repository.network.api.supportdepartmentsresponse.SupportDepartmentsResponse
import com.vishwaagrotech.digitalhr.repository.network.api.supportlistresponse.SupportListResponse
import com.vishwaagrotech.digitalhr.repository.network.api.tadadetailresponse.TadaDetailResponse
import com.vishwaagrotech.digitalhr.repository.network.api.tadalistresponse.TadaListResponse
import com.vishwaagrotech.digitalhr.repository.network.api.taskdetailresponse.TaskDetailResponse
import com.vishwaagrotech.digitalhr.repository.network.api.tasklistresponse.TaskListResponse
import com.vishwaagrotech.digitalhr.repository.network.api.teammeeting.TeamMeetingResponse
import com.vishwaagrotech.digitalhr.repository.network.api.teamsheet.TeamSheetResponse
import com.vishwaagrotech.digitalhr.repository.network.api.userprofile.ProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {
    @POST("login")
    @FormUrlEncoded
    suspend fun getLoginResponse(
        @Field("username") email: String,
        @Field("password") password: String,
        @Field("fcm_token") fcm: String,
        @Field("device_type") device: String,
        @Field("uuid") uuid: String,
    ): Response<LoginResponse>

    @GET("logout")
    @Headers("Accept: application/json")
    suspend fun getLogoutResponse(
        @Header("Authorization") token: String,
    ): Response<LogoutResponse>

    @GET("users/profile")
    @Headers("Accept: application/json")
    suspend fun getUserProfileResponse(
        @Header("Authorization") token: String,
    ): Response<ProfileResponse>

    @GET("holidays")
    @Headers("Accept: application/json")
    suspend fun getHolidayResponse(
        @Header("Authorization") token: String,
    ): Response<HolidayResponse>

    @GET("users/company/team-sheet")
    @Headers("Accept: application/json")
    suspend fun getTeamResponse(
        @Header("Authorization") token: String,
    ): Response<TeamSheetResponse>

    @GET("notifications")
    @Headers("Accept: application/json")
    suspend fun getNotification(
        @Header("Authorization") token: String,
        @Query("per_page") per_page: Int,
        @Query("page") page: Int
    ): Response<NoticationResponse>

    @GET("notices")
    @Headers("Accept: application/json")
    suspend fun getNotices(
        @Header("Authorization") token: String,
        @Query("per_page") per_page: Int,
        @Query("page") page: Int
    ): Response<NoticeResponse>

    @GET("dashboard")
    @Headers("Accept: application/json")
    suspend fun getDashboard(
        @Header("Authorization") token: String
    ): Response<DashboardResponse>

    @GET("leave-types")
    @Headers("Accept: application/json")
    suspend fun getLeaveType(
        @Header("Authorization") token: String
    ): Response<LeaveTypeResponse>

    @GET("static-page-content/{value}")
    @Headers("Accept: application/json")
    suspend fun getStaticPage(
        @Header("Authorization") token: String,
        @Path("value") value: String
    ): Response<StaticPageResponse>

    @POST("employees/check-in")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    suspend fun getCheckInResponse(
        @Header("Authorization") token: String,
        @Field("router_bssid") bssid: String,
        @Field("check_in_latitude") latitude: Double,
        @Field("check_in_longitude") longitude: Double
    ): Response<AttendanceStatusResponse>

    @POST("employees/check-out")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    suspend fun getCheckOutResponse(
        @Header("Authorization") token: String,
        @Field("router_bssid") bssid: String,
        @Field("check_out_latitude") latitude: Double,
        @Field("check_out_longitude") longitude: Double
    ): Response<AttendanceStatusResponse>

    @GET("employees/attendance-detail")
    @Headers("Accept: application/json")
    suspend fun getEmployeeAttendanceReport(
        @Header("Authorization") token: String,
        @Query("month") value: Int
    ): Response<EmployeeAttandanceReportResponse>

    @POST("leave-requests/store")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    suspend fun getLeaveRequestResponse(
        @Header("Authorization") token: String,
        @Field("leave_from") leave_from: String,
        @Field("leave_to") leave_to: String,
        @Field("leave_type_id") leave_type_id: Int,
        @Field("reasons") reasons: String,
        @Field("early_exit") early_exit: Int
    ): Response<LeaveRequestResponse>

    @GET("leave-requests/employee-leave-requests")
    @Headers("Accept: application/json")
    suspend fun getLeaveRequestListResponse(
        @Header("Authorization") token: String,
        @Query("leave_type") leave_type: String,
        @Query("month") month: String
    ): Response<LeaveRequestListResponse>

    @GET("leave-requests/employee-leave-calendar")
    @Headers("Accept: application/json")
    suspend fun getEmployeeLeaveCalendar(
        @Header("Authorization") token: String
    ): Response<EmployeeLeaveCalendarResponse>

    @GET("leave-requests/employee-leave-list")
    @Headers("Accept: application/json")
    suspend fun getEmployeeLeaveCalendarByDay(
        @Header("Authorization") token: String,
        @Query("leave_date") date: String
    ): Response<EmployeeLeaveCalendarByDayResponse>

    @POST("users/change-password")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Field("current_password") current_password: String,
        @Field("new_password") new_password: String,
        @Field("confirm_password") confirm_password: String,
    ): Response<ChangePasswordResponse>

    @POST("users/update-profile")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Field("name") current_password: String,
        @Field("email") new_password: String,
        @Field("address") address: String,
        @Field("phone") phone: String,
        @Field("gender") gender: String,
        @Field("dob") dob: String,
    ): Response<ProfileResponse>

    @POST("users/update-profile")
    @Headers("Accept: application/json")
    @Multipart
    suspend fun updateProfilePicture(
        @Header("Authorization") token: String,
        @Part avatar: MultipartBody.Part,
    ): Response<ProfileResponse>

    @GET("company-rules")
    @Headers("Accept: application/json")
    suspend fun getCompanyRulesResponse(
        @Header("Authorization") token: String
    ): Response<CompanyRulesResponse>

    @GET("support/department-lists")
    @Headers("Accept: application/json")
    suspend fun getSupportDepartmentsResponse(
        @Header("Authorization") token: String
    ): Response<SupportDepartmentsResponse>

    @GET("support/get-user-query-lists?per_page=500")
    @Headers("Accept: application/json")
    suspend fun getSupportListResponse(
        @Header("Authorization") token: String
    ): Response<SupportListResponse>

    @POST("support/query-store")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    suspend fun saveSupportResponse(
        @Header("Authorization") token: String,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("department_id") departmentId: String,
    ): Response<SupportSaveResponse>

    @GET("team-meetings{value}")
    @Headers("Accept: application/json")
    suspend fun getTeamMeeting(
        @Header("Authorization") token: String,
        @Path("value") value: String,
        @Query("per_page") per_page: Int,
        @Query("page") page: Int
    ): Response<TeamMeetingResponse>

    @GET("employee/tada-lists")
    @Headers("Accept: application/json")
    suspend fun getTadaList(
        @Header("Authorization") token: String,
    ): Response<TadaListResponse>

    @GET("employee/tada-details/{value}")
    @Headers("Accept: application/json")
    suspend fun getTadaDetail(
        @Header("Authorization") token: String,
        @Path("value") value: String
    ): Response<TadaDetailResponse>

    @POST("employee/tada/store")
    @Headers("Accept: application/json")
    @Multipart
    suspend fun saveTadaCreate(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("total_expense") total_expense: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<CreateTadaResponse>

    @POST("employee/tada/update")
    @Headers("Accept: application/json")
    @Multipart
    suspend fun editTadaCreate(
        @Header("Authorization") token: String,
        @Part("tada_id") value: RequestBody,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("total_expense") total_expense: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<CreateTadaResponse>

    @GET("employee/tada/delete-attachment/{value}")
    @Headers("Accept: application/json")
    suspend fun deleteAttachments(
        @Header("Authorization") token: String,
        @Path("value") value: String,
    ): Response<CreateTadaResponse>

    @GET("users/profile-detail/{value}")
    @Headers("Accept: application/json")
    suspend fun getEmployeeDetail(
        @Header("Authorization") token: String,
        @Path("value") value: String,
    ): Response<EmployeeDetailResponse>

    @GET("project-management-dashboard")
    @Headers("Accept: application/json")
    suspend fun getProjectDashboard(
        @Header("Authorization") token: String,
        @Query("tasks") tasks: String,
        @Query("projects") projects: String,
    ): Response<ProjectDashboardReponse>

    @GET("assigned-projects-list")
    @Headers("Accept: application/json")
    suspend fun getProjectList(
        @Header("Authorization") token: String,
    ): Response<ProjectListResponse>

    @GET("assigned-projects-detail/{value}")
    @Headers("Accept: application/json")
    suspend fun getProjectDetail(
        @Header("Authorization") token: String,
        @Path("value") value: String
    ): Response<ProjectDetailResponse>

    @GET("assigned-task-list")
    @Headers("Accept: application/json")
    suspend fun getTaskList(
        @Header("Authorization") token: String,
    ): Response<TaskListResponse>

    @GET("assigned-task-detail/{value}")
    @Headers("Accept: application/json")
    suspend fun getTaskDetail(
        @Header("Authorization") token: String,
        @Path("value") value: String
    ): Response<TaskDetailResponse>

    @GET("assigned-task-checklist/toggle-status/{value}")
    @Headers("Accept: application/json")
    suspend fun toggleChecklist(
        @Header("Authorization") token: String,
        @Path("value") value: String
    ): Response<GlobalResponse>

    @GET("assigned-task-detail/change-status/{value}")
    @Headers("Accept: application/json")
    suspend fun toggleTask(
        @Header("Authorization") token: String,
        @Path("value") value: String
    ): Response<GlobalResponse>

    @GET("assigned-task-comments")
    @Headers("Accept: application/json")
    suspend fun getCommentList(
        @Header("Authorization") token: String,
        @Query("task_id") taskId: String,
        @Query("per_page") per_page: String,
        @Query("page") page: String,
    ): Response<CommentListResponse>

    @POST("assigned-task/comments/store")
    @FormUrlEncoded
    @Headers("Accept: application/json")
    suspend fun saveComment(
        @Header("Authorization") token: String,
        @Field("task_id") taskId: String,
        @Field("comment_id") comment_id: String,
        @Field("description") description: String,
        @Field("mentioned[]") mentioned: List<String>,
    ): Response<SaveCommentResponse>

    @POST("assigned-task/comments/store")
    @FormUrlEncoded
    @Headers("Accept: application/json")
    suspend fun saveCommentWithoutMention(
        @Header("Authorization") token: String,
        @Field("task_id") taskId: String,
        @Field("comment_id") comment_id: String,
        @Field("description") description: String,
    ): Response<SaveCommentResponse>

    @GET("assigned-task/comment/delete/{value}")
    @Headers("Accept: application/json")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("value") value: String,
    ): Response<GlobalResponse>

    @GET("assigned-task/reply/delete/{value}")
    @Headers("Accept: application/json")
    suspend fun deleteReply(
        @Header("Authorization") token: String,
        @Path("value") value: String,
    ): Response<GlobalResponse>

}
