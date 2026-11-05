export const msalConfig = {
  auth: {
    clientId: "85e9fe43-66e4-4cff-9bfd-0a2721dd518c",
    authority: "https://login.microsoftonline.com/4da6743e-cd49-40ec-8ccd-0e4a2b2e1af2",
    redirectUri: "http://localhost:3000",
    postLogoutRedirectUri: "http://localhost:3000"
  },
  cache: {
    cacheLocation: "sessionStorage",
    storeAuthStateInCookie: false
  }
};

export const loginRequest = {
  scopes: [
    "api://f78031e4-c107-4535-a5ae-90f55c0e759a/access_as_user"
  ]
};

export const apiConfig = {
  baseUrl: "https://i4ni69dz15.execute-api.us-east-1.amazonaws.com"
};