export class AppError extends Error{
    constructor(message) {
        super(message);
    }
}




export class UnAuthorizedError extends AppError{}

export class NetworkError extends AppError{}

export  class ServerError extends AppError{}

export class ForbiddenError extends AppError{}