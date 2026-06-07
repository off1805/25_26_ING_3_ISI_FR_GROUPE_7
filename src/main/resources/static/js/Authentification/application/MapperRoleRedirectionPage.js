const ADMIN_REDIRECTION_PAGE       = "/admin/users";
const AP_REDIRECTION_PAGE          = "/ap/classes";
const TEACHER_REDIRECTION_PAGE     = "/teacher/dashboard";
const STUDENT_REDIRECTION_PAGE     = "/student/dashboard";
const SURVEILLANT_REDIRECTION_PAGE = "/surveillant/dashboard";

export function MapperRoleRedirectionPage(role) {
    switch (role) {
        case "ADMIN":       return ADMIN_REDIRECTION_PAGE;
        case "AP":          return AP_REDIRECTION_PAGE;
        case "TEACHER":     return TEACHER_REDIRECTION_PAGE;
        case "STUDENT":     return STUDENT_REDIRECTION_PAGE;
        case "SURVEILLANT": return SURVEILLANT_REDIRECTION_PAGE;
        default:            return "/";
    }
}