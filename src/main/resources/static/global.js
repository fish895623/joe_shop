function getTokenFromCookie() {
  const cookies = document.cookie.split(";");
  for (let cookie of cookies) {
    const [name, value] = cookie.trim().split("=");
    if (name === "token") {
      return value;
    }
  }
  return null;
}

function parseJwt(token) {
  try {
    if (!token) {
      console.info("No token provided");
      return null;
    }
    // Get the payload part (second part) of the JWT token
    const tokenParts = String(token).split(".");
    if (tokenParts.length !== 3) {
      return null;
    }
    const base64Url = tokenParts[1];
    // Replace non-url compatible chars with standard base64 chars
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    // Decode the base64 string
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map(function (c) {
          return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join(""),
    );

    // if token has expired, return null
    const payload = JSON.parse(jsonPayload);
    const exp = payload.exp;
    const currentTime = Math.floor(Date.now() / 1000);
    if (exp < currentTime) {
      return null;
    }

    // Parse the JSON string
    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error("Error parsing JWT token:", error);
    return null;
  }
}

function getUserRole(token) {
  if (!token) {
    return null;
  }

  const payload = parseJwt(token);
  if (!payload) {
    return null;
  }

  // Handle different role formats
  if (payload.role) {
    // Format 1: Simple role string
    return payload.role;
  }
  // Check for custom Spring Security format
  const authoritiesKey = Object.keys(payload).find(
    (key) => key.includes("authorities") || key.includes("role"),
  );

  if (authoritiesKey) {
    return payload[authoritiesKey];
  }

  return null;
}

// 로컬 스토리지에 값을 저장하고 다른 페이지로 이동하는 함수 ====================================
function redirectToViewWithValues(valueTitle, valueToSend, targetUrl) {
  // Store values in localStorage
  localStorage.setItem(valueTitle, JSON.stringify(valueToSend));

  // Redirect to the new URL
  window.location.href = targetUrl;
}

// 로컬 스토리지에서 값을 가져오는 함수 =======================================================
function getValuesFromLocalStorage(valueTitle) {
  return JSON.parse(localStorage.getItem(valueTitle));
}
