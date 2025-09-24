import { Injectable } from "@angular/core";
import { CanActivate, Router, ActivatedRouteSnapshot } from "@angular/router";
import { JwtHelperService } from "@auth0/angular-jwt";

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const token = localStorage.getItem("authToken"); 
    const jwt = new JwtHelperService();

    if (!token) {
      this.router.navigate(["/login"]);
      return false;
    }

    const decodedToken = jwt.decodeToken(token);

    if (!decodedToken?.role) {
      this.router.navigate(["/forbidden"]);
      return false;
    }

    const expectedRoles: string[] = route.data['expectedRoles'] || [];

    if (!expectedRoles.includes(decodedToken.role)) {
      this.router.navigate(["/forbidden"]);
      return false;
    }

    return true;
  }
}
