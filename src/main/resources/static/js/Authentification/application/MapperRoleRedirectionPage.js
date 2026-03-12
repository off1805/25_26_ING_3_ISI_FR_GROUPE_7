
const ADMIN_REDIRECTION_PAGE="/admin/dashboard";
const STUDENT_REDIRECTION_PAGE= "/student/dashboard";


export function MapperRoleRedirectionPage(role){
    switch (role){
            case "ADMIN":
                return ADMIN_REDIRECTION_PAGE;
            case "STUDENT":
                return STUDENT_REDIRECTION_PAGE;
        }
}