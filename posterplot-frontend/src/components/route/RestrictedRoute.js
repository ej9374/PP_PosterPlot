import { useEffect, useState } from "react";
import { Navigate, Outlet } from "react-router-dom";

const RestrictedRoute = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");
    setIsAuthenticated(!!token && !!userId);
  }, []);

  if (isAuthenticated === null) {
    return <div>LOADING...</div>;
  }

  return isAuthenticated ? <Navigate to="/" replace /> : <Outlet />;
};

export default RestrictedRoute;
